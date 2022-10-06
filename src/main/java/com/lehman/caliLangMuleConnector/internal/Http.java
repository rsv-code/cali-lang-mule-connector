/*
 * Copyright 2022 Austin Lehman (austin@rosevillecode.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.lehman.caliLangMuleConnector.internal;

import com.cali.Environment;
import com.cali.types.*;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Http {
    protected HttpCookieJar cookies = new HttpCookieJar();
    protected OkHttpClient client;

    public Http() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(cookies);
        this.client = builder.build();
    }

    public CaliType get(Environment env, ArrayList<CaliType> args) throws IOException {
        String url = ((CaliString)args.get(0)).getValue();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = this.client.newCall(request).execute();
        return this.processResponse(response);
    }

    public CaliType post(Environment env, ArrayList<CaliType> args) throws IOException {
        String url = ((CaliString)args.get(0)).getValue();
        String content = ((CaliString)args.get(1)).getValue();
        String mediaType = ((CaliString)args.get(2)).getValue();

        MediaType mtype = MediaType.get(mediaType);
        RequestBody body = RequestBody.create(content, mtype);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = this.client.newCall(request).execute();
        return this.processResponse(response);
    }

    private CaliMap processResponse(Response response) throws IOException {
        CaliMap ret = new CaliMap();
        ret.put("body", new CaliString(response.body().string()));

        CaliMap info = new CaliMap();
        ret.put("info", info);
        info.put("headers", this.getHeaders(response));
        info.put("protocol", new CaliString(response.protocol().toString()));
        info.put("isRedirect", new CaliBool(response.isRedirect()));
        info.put("isSuccessful", new CaliBool(response.isSuccessful()));
        info.put("responseCode", new CaliInt(response.code()));
        info.put("cookies", this.getCookies());

        return ret;
    }

    private CaliMap getHeaders(Response response) {
        CaliMap headers = new CaliMap();
        for (String str : response.headers().names()) {
            List<String> lst = response.headers().toMultimap().get(str);
            if (lst.size() == 1) {
                headers.put(str, new CaliString(lst.get(0).replaceAll("\"","\\\\\"")));
            } else {
                CaliList cl = new CaliList();
                for (int i = 0; i < lst.size(); i++) {
                    cl.add(new CaliString(lst.get(i).replaceAll("\"","\\\\\"")));
                }
                headers.put(str, cl);
            }
        }
        return headers;
    }

    private CaliList getCookies() {
        CaliList cookieList = new CaliList();
        for (Cookie cookie : this.cookies.getCookies()) {
            CaliMap mc = new CaliMap();
            mc.put("domain", new CaliString(cookie.domain()));
            mc.put("name", new CaliString(cookie.name()));
            mc.put("value", new CaliString(cookie.value()));
            mc.put("path", new CaliString(cookie.path()));
            mc.put("secure", new CaliBool(cookie.secure()));
            mc.put("expiresAt", new CaliInt(cookie.expiresAt()));
            mc.put("domain", new CaliBool(cookie.persistent()));
            mc.put("hostOnly", new CaliBool(cookie.hostOnly()));
            mc.put("httpOnly", new CaliBool(cookie.httpOnly()));
            cookieList.add(mc);
        }
        return cookieList;
    }
}

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
import com.cali.ast.caliException;
import com.cali.stdlib.CBuffer;
import com.cali.stdlib.CDate;
import com.cali.types.*;

import java.sql.*;
import java.util.ArrayList;

public class Jdbc {
    protected Connection con;

    protected String driver = "";
    protected String url = "";
    protected String userName = "";
    protected String password = "";

    public Jdbc() { }

    public CaliType setDriver(Environment env, ArrayList<CaliType> args) {
        this.driver = ((CaliString)args.get(0)).getValue();
        return env.getCurObj() != null ? env.getCurObj() : new CaliNull();
    }

    public CaliType setUrl(Environment env, ArrayList<CaliType> args) {
        this.url = ((CaliString)args.get(0)).getValue();
        return env.getCurObj() != null ? env.getCurObj() : new CaliNull();
    }

    public CaliType setUserName(Environment env, ArrayList<CaliType> args) {
        this.userName = ((CaliString)args.get(0)).getValue();
        return env.getCurObj() != null ? env.getCurObj() : new CaliNull();
    }

    public CaliType setPassword(Environment env, ArrayList<CaliType> args) {
        this.password = ((CaliString)args.get(0)).getValue();
        return env.getCurObj() != null ? env.getCurObj() : new CaliNull();
    }

    public CaliType getDriver(Environment env, ArrayList<CaliType> args) {
        return new CaliString(this.driver);
    }

    public CaliType getUrl(Environment env, ArrayList<CaliType> args) {
        return new CaliString(this.url);
    }

    public CaliType getUserName(Environment env, ArrayList<CaliType> args) {
        return new CaliString(this.userName);
    }

    public CaliType getPassword(Environment env, ArrayList<CaliType> args) {
        return new CaliString(this.password);
    }

    public CaliType setConnectionInfo(Environment env, ArrayList<CaliType> args) {
        this.driver = ((CaliString)args.get(0)).getValue();
        this.url = ((CaliString)args.get(1)).getValue();
        this.userName = ((CaliString)args.get(2)).getValue();
        this.password = ((CaliString)args.get(3)).getValue();
        return env.getCurObj() != null ? env.getCurObj() : new CaliNull();
    }

    public CaliType select(Environment env, ArrayList<CaliType> args) throws SQLException, caliException {
        if (this.con != null) {
            String query = ((CaliString) args.get(0)).getValue();
            CaliList params = (CaliList) args.get(1);

            PreparedStatement ps = this.con.prepareStatement(query);
            this.addParamsToPreparedStatement(ps, params);
            // execute the query.
            ResultSet rs = ps.executeQuery();

            // returned object
            CaliMap ret = new CaliMap();

            // First find col info
            CaliList cols = new CaliList();
            ret.put("cols", cols);
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 0; i < rsmd.getColumnCount(); i++) {
                CaliMap colInfo = new CaliMap();
                colInfo.put("name", new CaliString(rsmd.getColumnName(i + 1)));
                colInfo.put("tableName", new CaliString(rsmd.getTableName(i + 1)));
                colInfo.put("label", new CaliString(rsmd.getColumnLabel(i + 1)));
                colInfo.put("type", new CaliString((rsmd.getColumnTypeName(i + 1))));
                colInfo.put("precision", new CaliInt(rsmd.getPrecision(i + 1)));
                colInfo.put("autoIncrement", new CaliBool(rsmd.isAutoIncrement(i + 1)));
                colInfo.put("caseSensitive", new CaliBool(rsmd.isCaseSensitive(i + 1)));
                colInfo.put("currency", new CaliBool(rsmd.isCurrency(i + 1)));
                colInfo.put("nullable", new CaliInt(rsmd.isNullable(i + 1)));
                cols.add(colInfo);
            }

            CaliList rows = new CaliList();
            ret.put("rows", rows);
            while (rs.next()) {
                CaliList row = new CaliList();
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                /*
-7	BIT
-6	TINYINT
-5	BIGINT
-4	LONGVARBINARY
-3	VARBINARY
-2	BINARY
-1	LONGVARCHAR
0	NULL
1	CHAR
2	NUMERIC
3	DECIMAL
4	INTEGER
5	SMALLINT
6	FLOAT
7	REAL
8	DOUBLE
12	VARCHAR
91	DATE
92	TIME
93	TIMESTAMP
1111 	OTHER
                 */
                    int colType = rsmd.getColumnType(i + 1);
                    if (colType == 1 || colType == 12 || colType == -1) {
                        row.add(new CaliString(rs.getString(i + 1)));
                    } else if (colType == 2 || colType == 3) {
                        row.add(new CaliString(rs.getBigDecimal(i + 1).toString()));
                    } else if (colType == -7) {
                        row.add(new CaliBool(rs.getBoolean(i + 1)));
                    } else if (colType == -6 || colType == 5 || colType == 4) {
                        row.add(new CaliInt(rs.getLong(i + 1)));
                    } else if (colType == 7 || colType == 6 || colType == 8) {
                        row.add(new CaliDouble(rs.getDouble(i + 1)));
                    } else if (colType == 91 || colType == 92 || colType == 93) {
                        CaliObject co = (CaliObject)env.getClassByName("date").instantiate(env, false, new CaliList());
                        CDate dt = (CDate) co.getExternObject();
                        dt.setTime(rs.getDate(i + 1).getTime());
                        row.add(co);
                    } else if (colType == 0) {
                        row.add(new CaliNull());
                    } else if (colType == -4 || colType == -3 || colType == -2) {
                        CaliObject co = (CaliObject)env.getClassByName("buffer").instantiate(env, false, new CaliList());
                        CBuffer buff = (CBuffer)co.getExternObject();
                        buff.setBuffer(rs.getBytes(i + 1));
                        row.add(co);
                    } else {
                        throw new caliException("jdbc.select(): Unknown column type " + colType + " '" + rsmd.getColumnTypeName(i + 1) + "' found.");
                    }
                }
                rows.add(row);
            }
            rs.close();
            ps.close();

            return ret;
        } else {
            throw new caliException("jdbc.select(): Connection is closed.");
        }
    }

    public CaliType update(Environment env, ArrayList<CaliType> args) throws SQLException, caliException {
        if (this.con != null) {
            String query = ((CaliString) args.get(0)).getValue();
            CaliList params = (CaliList) args.get(1);

            PreparedStatement ps = this.con.prepareStatement(query);
            this.addParamsToPreparedStatement(ps, params);
            // execute the query.
            int rowsAffected = ps.executeUpdate();
            return new CaliInt(rowsAffected);
        } else {
            throw new caliException("jdbc.update(): Connection is closed.");
        }
    }

    public CaliType connect(Environment env, ArrayList<CaliType> args) {
        try {
            Class.forName(this.driver);
            this.con = DriverManager.getConnection(this.url, this.userName, this.password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return env.getCurObj() != null ? env.getCurObj() : new CaliNull();
    }

    public CaliType disconnect(Environment env, ArrayList<CaliType> args) {
        if (this.con != null) {
            try {
                this.con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                this.con = null;
            }
        }
        return env.getCurObj() != null ? env.getCurObj() : new CaliNull();
    }

    private void addParamsToPreparedStatement(PreparedStatement ps, CaliList params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            CaliType param = params.getValue().get(i);
            if (param.getType() == cType.cString)
                ps.setString(i + 1, ((CaliString)param).getValue());
            else if (param.getType() == cType.cBool)
                ps.setBoolean(i + 1, ((CaliBool)param).getValue());
            else if (param.getType() == cType.cInt)
                ps.setLong(i + 1, ((CaliInt)param).getValue());
            else if (param.getType() == cType.cDouble)
                ps.setDouble(i + 1, ((CaliDouble)param).getValue());
            else if (param.getType() == cType.cObject && ((CaliObject)param).getClassDef().instanceOf("date")) {
                CDate dt = (CDate)((CaliObject)param).getExternObject();
                ps.setDate(i + 1, new Date(dt.getTime()));
            } else if (param.getType() == cType.cNull) {
                ps.setNull(i + 1, 0);
            } else {
                // No good, not sure what to do here ... perhaps I should throw an exception instead.
                ps.setString(i + 1, param.getValueString());
            }
        }
    }
}

package com.example.tesis_proyecto.dto;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class AlertSeverityType implements UserType<AlertSeverity> {

    @Override
    public int getSqlType() { return Types.OTHER; }

    @Override
    public Class<AlertSeverity> returnedClass() { return AlertSeverity.class; }

    @Override
    public AlertSeverity nullSafeGet(ResultSet rs, int position,
                                     SharedSessionContractImplementor session,
                                     Object owner) throws SQLException {
        String value = rs.getString(position);
        // ✅ Sin toUpperCase — coincide exactamente con el ENUM de PostgreSQL
        return value == null ? null : AlertSeverity.valueOf(value);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, AlertSeverity value,
                            int index,
                            SharedSessionContractImplementor session) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            // ✅ Types.OTHER hace el cast correcto al enum nativo de PostgreSQL
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override public boolean equals(AlertSeverity x, AlertSeverity y) { return x == y; }
    @Override public int hashCode(AlertSeverity x) { return x.hashCode(); }
    @Override public AlertSeverity deepCopy(AlertSeverity value) { return value; }
    @Override public boolean isMutable() { return false; }
    @Override public Serializable disassemble(AlertSeverity value) { return value.name(); }
    @Override public AlertSeverity assemble(Serializable cached, Object owner) {
        return AlertSeverity.valueOf((String) cached);
    }
}
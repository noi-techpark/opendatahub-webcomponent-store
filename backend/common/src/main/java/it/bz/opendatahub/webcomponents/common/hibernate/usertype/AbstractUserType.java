// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.hibernate.usertype;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Common code of most UserTypes.
 */
public abstract class AbstractUserType<T> implements UserType<T> {
    protected static final ObjectMapper objectMapper = new ObjectMapper();
    protected static final TypeFactory typeFactory = objectMapper.getTypeFactory();

    private final JavaType returnType;

    protected AbstractUserType() {
        returnType = typeFactory.constructType(returnedClass());
    }

    @Override
    public T nullSafeGet(ResultSet resultSet, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        final String s = resultSet.getString(position);

        if (s == null) {
            return null;
        }

        try {
            return objectMapper.readValue(s, returnType);
        }
        catch (IOException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, T o, int i, SharedSessionContractImplementor session) throws SQLException {
        try {
            PGobject pgo = new PGobject();
            pgo.setType("jsonb");
            pgo.setValue(objectMapper.writeValueAsString(o));

            preparedStatement.setObject(i, pgo);
        }
        catch (IOException e) {
            throw new HibernateException(e);
        }
    }

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public boolean equals(T o, T o1) {
        if(o == null || o1 == null) {
            return false;
        }
        else {
            return o.equals(o1);
        }
    }

    @Override
    public int hashCode(T o) {
        if(o == null) {
            throw new HibernateException("hashCode() => null");
        }
        return o.hashCode();
    }

    @Override
    public T deepCopy(T o) {
        return o;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(T o) {
        return (Serializable) o;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T assemble(Serializable serializable, Object o) {
        return (T) serializable;
    }

    @Override
    public T replace(T o, T o1, Object o2) {
        return o;
    }

}

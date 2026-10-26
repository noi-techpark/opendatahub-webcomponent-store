// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl;

import it.bz.opendatahub.webcomponents.common.data.struct.Author;
import it.bz.opendatahub.webcomponents.common.hibernate.usertype.ListUserType;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public final class ListAuthorUserType extends ListUserType<Author> {
    public static final String NAME = "ListAuthorUserType";

    @Override
    public List<Author> nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
        final String s = rs.getString(position);

        if(s == null) {
            return null;
        }

        try {
            return Arrays.asList(objectMapper.readValue(s, Author[].class));
        }
        catch (IOException e) {
            throw new HibernateException(e);
        }
    }
}

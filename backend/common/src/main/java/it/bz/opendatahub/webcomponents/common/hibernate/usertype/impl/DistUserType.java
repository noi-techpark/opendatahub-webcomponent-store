// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl;

import it.bz.opendatahub.webcomponents.common.data.struct.Dist;
import it.bz.opendatahub.webcomponents.common.hibernate.usertype.AbstractUserType;

public final class DistUserType extends AbstractUserType<Dist> {
    public static final String NAME = "DistUserType";

    @Override
    public Class<Dist> returnedClass() {
        return Dist.class;
    }
}

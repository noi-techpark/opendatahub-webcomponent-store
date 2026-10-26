// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl;

import it.bz.opendatahub.webcomponents.common.data.struct.Configuration;
import it.bz.opendatahub.webcomponents.common.hibernate.usertype.AbstractUserType;

public final class ConfigurationUserType extends AbstractUserType<Configuration> {
    public static final String NAME = "ConfigurationUserType";

	@Override
    public Class<Configuration> returnedClass() {
        return Configuration.class;
    }
}

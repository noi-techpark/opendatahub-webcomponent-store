// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.data.model;

import it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl.ListStringUserType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "spdx")
public class SpdxLicenseModel {
    private String reference;

    private Boolean isDeprecatedLicenseId;

    private String detailsUrl;

    private String referenceNumber;

    private String name;

    @Id
    private String licenseId;

    @Type(ListStringUserType.class)
    private List<String> seeAlso;

    private Boolean isOsiApproved;
}

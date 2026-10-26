// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.data.model;

import it.bz.opendatahub.webcomponents.common.data.model.id.WebcomponentVersionId;
import it.bz.opendatahub.webcomponents.common.data.struct.Configuration;
import it.bz.opendatahub.webcomponents.common.data.struct.Dist;
import it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl.ConfigurationUserType;
import it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl.DistUserType;
import it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl.StringUserType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@IdClass(WebcomponentVersionId.class)
@Table(name = "webcomponent_version")
public class WebcomponentVersionModel {
    @Id
    private String webcomponentUuid;

    @Id
    private String versionTag;

    private Date releaseTimestamp;

    @Type(DistUserType.class)
    private Dist dist;

    @Type(ConfigurationUserType.class)
    private Configuration configuration;

    private Boolean deleted;

    private Integer distSizeTotalKb = 0;

	@Type(StringUserType.class)
	private String lighthouseMetricsMobileData;

	private LocalDateTime lighthouseMetricsMobileDatetime;

	private Integer lighthouseMobilePerformanceRating = 0;

	@Type(StringUserType.class)
	private String lighthouseMetricsDesktopData;

	private LocalDateTime lighthouseMetricsDesktopDatetime;

	private Integer lighthouseDesktopPerformanceRating = 0;

	private Boolean lighthouseUpdateRequired = true;

	private String readMe;

	private String licenseAgreement;
}

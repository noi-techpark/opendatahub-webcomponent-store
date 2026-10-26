// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

package it.bz.opendatahub.webcomponents.common.data.model;

import it.bz.opendatahub.webcomponents.common.data.struct.Author;
import it.bz.opendatahub.webcomponents.common.hibernate.usertype.impl.ListAuthorUserType;
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
@Table(name = "webcomponent")
public class WebcomponentModel {
    @Id
    private String uuid;

    private String shortName;

    private String title;

    private String description;

    private String descriptionAbstract;

    private String repositoryUrl;

    @Type(ListAuthorUserType.class)
    private List<Author> copyrightHolders;

    private String image;

    private String license;

    @Type(ListAuthorUserType.class)
    private List<Author> authors;

    @Type(ListStringUserType.class)
    private List<String> searchTags;

    private Boolean deleted;

    private Boolean highlighted;

	private String theme;
}

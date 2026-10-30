<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: AGPL-3.0-or-later
-->

<template>
  <div>
    <div>
      <div class="container container-extended p-4 pb-0">
        <div class="row">
          <div class="col-md-4 col-sm-12 mt-0 pb-2">
            <form @submit.prevent="termSubmitted()">
              <div
                id="widget-search"
                :class="{ active: searchTerm && searchTerm !== '' }"
                class="full-height d-flex justify-content-between font-large pb-2"
              >
                <div class="full-width pr-2 search-input">
                  <input
                    ref="searchTermInput"
                    v-model="internalSearchTerm"
                    @input="termUpdated"
                    type="text"
                    placeholder="Search"
                    style="outline: none"
                    class="p-0 full-width search-text"
                  />
                </div>

                <img
                  v-if="searchTerm !== ''"
                  src="/icons/close_black.svg"
                  class="clear-icon cursor-pointer"
                  @click="clearSearch"
                />
                <svg
                  v-else
                  style="margin-top: 0.15rem; height: 1.5rem"
                  xmlns="http://www.w3.org/2000/svg"
                  width="22"
                  height="22"
                  viewBox="0 0 22 22"
                  class="search-image"
                  @click="termSubmitted()"
                >
                  <defs>
                    <style>
                      .a,
                      .b {
                        fill: none;
                      }
                      .b {
                        stroke: #2f2f2f;
                        stroke-miterlimit: 10;
                        stroke-width: 2px;
                      }
                    </style>
                  </defs>
                  <g transform="translate(13.391 13.845)">
                    <g transform="translate(-12.5 -12.845)">
                      <g transform="translate(-0.891 -1)">
                        <g transform="translate(0.929 0.767)">
                          <ellipse
                            class="b"
                            cx="7.535"
                            cy="7.535"
                            rx="7.535"
                            ry="7.535"
                            transform="translate(0.891 1)"
                          />
                          <line
                            class="b"
                            x1="5.699"
                            y1="5.699"
                            transform="translate(13.555 14.171)"
                          />
                        </g>
                      </g>
                    </g>
                  </g>
                </svg>
              </div>
            </form>
          </div>
          <div class="col-md-4 col-sm-12 pb-2">
            <div id="tags-zone">
              <div
                id="widget-tags"
                :class="{
                  active:
                    userSelectedTags &&
                    userSelectedTags.length > 0 &&
                    userSelectedTags[0] !== 'any',
                }"
                style="cursor: pointer"
                class="full-height d-flex justify-content-between font-large pb-2 align-items-center"
                @click="searchTagsVisible = !searchTagsVisible"
              >
                <span v-if="userSelectedTags.length === 0" class="filter-text"
                  >Categories</span
                >
                <span v-else class="filter-text-light text-capitalize">{{
                  userSelectedTags.join(', ')
                }}</span>

                <div style="height: 100%">
                  <img
                    v-if="selectedTags.length > 0"
                    src="/icons/close_black.svg"
                    class="clear-icon"
                    style="margin-right: 15px"
                    @click.stop="clearTags"
                  />
                  <span style="padding-top: 15px">
                    <span
                      class="chevron semi-bold mr-2"
                      :class="[searchTagsVisible ? 'top' : 'bottom']"
                    ></span>
                  </span>
                </div>
              </div>
              <b-collapse
                id="tag-collapse"
                v-model="searchTagsVisible"
                style="
                  position: absolute;
                  border-left: 1px solid #e8ecf1;
                  border-bottom: 1px solid #e8ecf1;
                  border-right: 1px solid #e8ecf1;
                  max-height: 500px;
                  overflow-y: scroll;
                "
                class="shadow"
              >
                <div class="m-4">
                  <b-form-checkbox-group
                    id="checkbox-group-2"
                    v-model="userSelectedTags"
                    name="flavour-2"
                    class="text-capitalize d-flex flex-column"
                    @input="tagsUpdated"
                  >
                    <b-form-checkbox
                      v-for="tag in availableSearchTags"
                      :key="tag"
                      :value="tag"
                      >{{ tag }}</b-form-checkbox
                    >
                  </b-form-checkbox-group>
                </div>
              </b-collapse>
            </div>
          </div>
          <div class="col-md-4 col-sm-12 pb-2">
                <div id="sorting-zone">
                    <div id="widget-sorting"
                        :class="{
                        active:
                            userSelectedSortingCondition
                        }"
                        class="full-height d-flex justify-content-between font-large pb-2 align-items-center cursor-pointer" 
                        @click="searchSortingVisible = !searchSortingVisible"
                    >
                        <span v-if="!userSelectedSortingCondition" class="filter-text"> Sort</span>
                        <span v-else class="filter-text-light text-capitalize">{{ getSortingLabelByKey(userSelectedSortingCondition) }}</span>
                        <div style="height: 100%">
                            <img
                                v-if="userSelectedSortingCondition"
                                src="/icons/close_black.svg"
                                class="clear-icon"
                                style="margin-right: 15px"
                                @click.stop="clearSorting"
                            />
                            <span style="padding-top: 15px">
                                <span
                                    class="chevron semi-bold mr-2"
                                    :class="[searchSortingVisible ? 'top' : 'bottom']"
                                ></span>
                            </span>
                        </div>
                        
                    </div>
                    <b-collapse
                        id="sorting-collapse"
                        v-model="searchSortingVisible"
                        style="
                            position: absolute;
                            border-left: 1px solid #e8ecf1;
                            border-bottom: 1px solid #e8ecf1;
                            border-right: 1px solid #e8ecf1;
                            max-height: 500px;
                            overflow-y: none;
                        "
                        class="shadow  cursor-pointer"
                    >
                        <div class="m-4">
                            <div class="row">
                                <div class="col">
                                    <strong>Field</strong>
                                    <b-form-group
                                        id="checkbox-group-3"
                                        name="flavour-3"
                                        class="text-capitalize d-flex flex-column mt-2"
                                        >
                                        <b-form-radio
                                            v-model="userSelectedSortingCondition"
                                            v-for="sCondition in availableSortings"
                                            :key="sCondition.key"
                                            :value="sCondition.key"
                                            @change="sortingUpdated"
                                        >{{ sCondition.label }}</b-form-radio>
                                    </b-form-group>
                                </div>
                                <div class="col">
                                    <strong>Ordering</strong>
                                    <b-form-group
                                        id="checkbox-group-3"
                                        name="flavour-3"
                                        class="text-capitalize d-flex flex-column mt-2"
                                        >
                                        <b-form-radio v-model="userSelectedSortingOrder" value="asc" @change="changeSortingOrder('asc')">
                                            Ascending
                                        </b-form-radio>
                                        <b-form-radio v-model="userSelectedSortingOrder" value="desc" @change="changeSortingOrder('desc')">
                                            Descending
                                        </b-form-radio>
                                    </b-form-group>
                                </div>
                            </div>
                        </div>
                    </b-collapse>
                </div>
            </div>
            
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import Vue, { PropType } from 'vue';

export default Vue.extend({
  props: {
    selectedTags: {
      default: () => {
        return [];
      },
      type: Array as PropType<Array<string>>,
    },
    searchTerm: { default: '', type: String },
    selectedSorting: {
      default: () => {
        return {
            condition:null,
            order:null
        };
      },
      type: Object,
    },
    focusSearch: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      userSelectedSortingOrder: (this.selectedSorting.order) ? this.selectedSorting.order : null,
      userSelectedSortingCondition: (this.selectedSorting.condition) ? this.selectedSorting.condition : null,
      internalSearchTerm: this.searchTerm,
      oldSearchTerm: this.searchTerm,
      userSelectedTags: this.selectedTags,
      searchTagsVisible: false,
      searchSortingVisible: false,
    };
  },
  computed: {
    availableSortingsMap(): Object {
        let obj = {}
        this.availableSortings.forEach((el) => {
            obj[el.key] = el
        });
        return obj;
    },
    availableSortings(): Array<any> {
      return [
       {
        key:'title',
        label:'Name'
       },
       {
        key:'releaseTimestamp',
        label:'Release date'
       } 
      ]
    },
    availableSearchTags(): Array<string> {
      return this.$store.getters['searchtags/getSearchtags'];
    },
  },
  watch: {
    searchTerm(value) {
      this.internalSearchTerm = value;
      this.ol = value;
    },
    selectedTags(value) {
      this.userSelectedTags = value;
    },
  },
  mounted() {
    //  this.loadSearchTags();

    if (this.focusSearch) {
      this.focusInput();
    }

    window.addEventListener('click', this.tagCollapseListener);
    window.addEventListener('click', this.sortingCollapseListener);
  },
  methods: {
    changeSortingOrder(val){
        this.userSelectedSortingOrder = val;
        this.sortingUpdated();
    },
    getSortingLabelByKey(key){
        if(this.availableSortingsMap[key]){
            return this.availableSortingsMap[key].label;
        }

        return '-';
    },
    sortingCollapseListener(e) {
      if (!document.getElementById('sorting-zone')) {
        return;
      }
      if (!document.getElementById('sorting-zone').contains(e.target)) {
        this.searchSortingVisible = false;
      }
    },
    tagCollapseListener(e) {
      if (!document.getElementById('tags-zone')) {
        return;
      }
      if (!document.getElementById('tags-zone').contains(e.target)) {
        this.searchTagsVisible = false;
      }
    },
    /* async loadSearchTags() {
      this.availableSearchTags = await this.$api.searchtag.listAll();
      this.isLoaded = true;
    }, */
    focusInput() {
      this.$refs.searchTermInput.focus();
    },
    sortingUpdated() {
        if(!this.userSelectedSortingOrder){
            this.userSelectedSortingOrder = 'asc';
        }

        this.$emit('sorting-updated', {
            sorting: {condition:this.userSelectedSortingCondition,order:this.userSelectedSortingOrder},
            tags: this.userSelectedTags,
            term: this.internalSearchTerm,
        });
    },
    tagsUpdated() {
      this.$emit('tags-updated', {
        sorting: {condition:this.userSelectedSortingCondition,order:this.userSelectedSortingOrder},
        tags: this.userSelectedTags,
        term: this.internalSearchTerm,
      });
    },
    termUpdated() {
      if (this.isNewSearchTerm()) {
        this.oldSearchTerm = this.internalSearchTerm;
        this.$emit('term-updated', {
          sorting: {condition:this.userSelectedSortingCondition,order:this.userSelectedSortingOrder},
          tags: this.userSelectedTags,
          term: this.internalSearchTerm,
        });
      }
    },
    termSubmitted() {
      this.oldSearchTerm = this.internalSearchTerm;
      this.$emit('term-submitted', {
        sorting: {condition:this.userSelectedSortingCondition,order:this.userSelectedSortingOrder},
        tags: this.userSelectedTags,
        term: this.internalSearchTerm,
      });
    },
    isNewSearchTerm() {
      return this.internalSearchTerm !== this.oldSearchTerm;
    },
    clearSearch() {
      this.internalSearchTerm = '';
    },
    clearTags() {
      this.userSelectedTags = [];
    },
    clearSorting() {
      this.userSelectedSortingCondition = null;
      this.userSelectedSortingOrder = null;
      this.sortingUpdated()
    },
  },
});
</script>

<style>
#tag-collapse, #sorting-collapse {
  background-color: white;
  width: 95%;
  font-size: large;
  z-index: 100;
}

.custom-checkbox {
  padding-bottom: 8px;
}

.clear-icon {
  margin-bottom: 5px;
}
</style>

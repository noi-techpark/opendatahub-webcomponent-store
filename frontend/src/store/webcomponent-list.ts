// SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>
//
// SPDX-License-Identifier: AGPL-3.0-or-later

import { ActionTree, GetterTree, MutationTree } from 'vuex';
import { WebcomponentEntryModel } from '../domain/model/WebcomponentEntryModel';
import { Page } from '../domain/repository/PagingAndSorting';
import { $api } from '~/utils/api-accessor';

export const state = () => ({
  sorting:{
    condition:'name',
    order:'asc',
  },
  webcomponentsList: {
    empty: true,
    first: true,
    last: true,
    number: 0,
    numberOfElements: 0,
    size: 0,
    totalElements: 0,
    totalPages: 0,
    content: [],
  } as Page<WebcomponentEntryModel>,
});

export type RootState = ReturnType<typeof state>;

export const getters: GetterTree<RootState, RootState> = {
  getLoadedPage: (state) => state.webcomponentsList,
};

export const mutations: MutationTree<RootState> = {
  SET_LOADED_PAGE: (state, webcomponentsList: Page<WebcomponentEntryModel>) => {
    if(state.sorting.condition && state.sorting.order){
        webcomponentsList.content.sort((a,b)=>{
            let evaluableA;
            let evaluableB;
            if(state.sorting.condition == 'releaseTimestamp'){
                evaluableA = a.currentVersion.releaseTimestamp;
                evaluableB = b.currentVersion.releaseTimestamp;
            }else{
                evaluableA = a[state.sorting.condition];
                evaluableB = b[state.sorting.condition];
            }
            let orderingWeight = 1;
            if(state.sorting.order == 'desc'){
                orderingWeight = -1;
            }
            if(evaluableA < evaluableB){
                return -orderingWeight;
            }else if(evaluableA > evaluableB){
                return orderingWeight;
            }
            return 0;
        })
    }
    state.webcomponentsList = webcomponentsList;
  },
  SET_SORTING:(state,sorting) =>{
    state.sorting = sorting;
  }
};

let latestRequest = 0;

export const actions: ActionTree<RootState, RootState> = {
    async loadPage({ commit }, { pageRequest,sorting, filter }) {
        commit('SET_SORTING', sorting);
        const requestId = ++latestRequest;
        const result = await $api.webcomponent.findAllPaged(pageRequest, filter);
        if (requestId !== latestRequest) {
            return;
        }

        commit('SET_LOADED_PAGE', result);
    },
};

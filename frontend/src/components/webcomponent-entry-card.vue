<!--
SPDX-FileCopyrightText: NOI Techpark <digital@noi.bz.it>

SPDX-License-Identifier: AGPL-3.0-or-later
-->

<template>
      <div class="flip-card" :class="{'flipped': isFlipped}">
          <div class="flip-card-inner">
              <div class="flip-card-front">
                <div class="overlay" v-if="!isFlipped"></div>
                <div class="aspect-box">
                    <div
                        :style="
                        'background-image: url(' +
                        getLogo +
                        '); background-position: center;'
                        "
                        class="aspect-container"
                    ></div>
                </div>
                <div class="front-card-buttons-container" v-if="!isFlipped">
                    <div class="front-card-button" @click="toggleFlipped()">
                        Preview
                    </div>
                    <div class="front-card-button">
                        <nuxt-link :to="returnPath" style="color: inherit; text-decoration: inherit">Details</nuxt-link>
                    </div>
                    <!-- <div class="front-card-button">
                        Share
                    </div> -->
                </div>
                <div class="front-content-container" v-if="!isFlipped">
                    <div class="title" :id="'more-options-zone'+uuKey+entry.uuid">
                        {{ entry.title }}
                        <div style="position: relative; display: inline;">
                            <img src="/icons/three-dots.svg" alt="..." @click="openMoreOptions(uuKey+entry.uuid)" />
                            <b-collapse
                                :id="'sorting-collapse'+uuKey+entry.uuid"
                                v-model="showMoreOptions"
                                class="mode-option-container"
                            >
                                <div class="m-2">
                                    <div class="row mb-1">
                                        <div class="col cursor-pointer">
                                            <img src="/icons/eye-outline.svg" class="mode-option-icon" alt="Show details"/>
                                            <nuxt-link :to="returnPath" style="color: inherit; text-decoration: inherit">Details</nuxt-link>
                                        </div>
                                    </div>
                                    <div class="row mb-1" @click="copyCode(uuKey+entry.uuid,false,$event)">
                                        <div class="col cursor-pointer">
                                            <img v-if="!showCheckIcon" src="/icons/content-copy.svg" class="mode-option-icon" alt="Copy details link"/>
                                            <img v-else="showCheckIcon" src="/icons/check.svg" class="mode-option-icon" alt="Link copied"/>
                                            Share
                                        </div>
                                    </div>
                                    <div class="row mb-1" @click="copyEmbedCode(uuKey+entry.uuid, false,$event)">
                                        <div class="col cursor-pointer">
                                            <img src="/icons/code-tags.svg" class="mode-option-icon" alt="Copy embed code"/>
                                            <nuxt-link :to="returnEmbedPath" style="color: inherit; text-decoration: inherit">Embed</nuxt-link>
                                        </div>
                                        <!-- <div class="col cursor-pointer">
                                            <img v-if="!showEmbedCheckIcon" src="/icons/code-tags.svg" class="mode-option-icon" alt="Copy embed code"/>
                                            <img v-else="showEmbedCheckIcon" src="/icons/check.svg" class="mode-option-icon" alt="Embed code copied"/>
                                            Embed
                                        </div> -->
                                    </div>
                                </div>
                            </b-collapse>
                        </div>
                    </div>
                    <div class="categories">
                        <div v-for="tag in entry.searchTags"
                            :key="tag"
                            class="category-badge">
                            {{ tag }}
                        </div>
                    </div>
                    <div class="description" v-if="!isFlipped">
                        {{ entry.descriptionAbstract }}
                    </div>
                </div>
              </div>
              <div class="flip-card-back">
                <div class="wc-iframe-container" :id="'twrap'+uuKey+entry.uuid">
                    <iframe
                        :id="'tframe'+uuKey+entry.uuid"
                        class="wc-iframe"
                        style="min-height: 15rem"
                        title="iframe-preview"
                    ></iframe>
                </div>
                <div class="wc-command-bar container-fluid">
                    <div class="row back-card-buttons-container">
                        <div class="col-2 back-card-button" style="padding-left:1rem; padding-top: 0.4rem;" @click="toggleFlipped()">
                            <img src="/icons/back-icon.svg" alt="back" style="width:1.3rem;" />
                        </div>
                        <div class="col back-card-button">
                            <nuxt-link :to="returnPath" style="color: inherit; text-decoration: inherit">details</nuxt-link>
                        </div>
                        <div :id="'copy-link'+uuKey+entry.uuid" class="col back-card-button" @click="copyCode(uuKey+entry.uuid,true,$event)">
                            Share
                        </div>                       
                        <div class="col back-card-button" @click="copyEmbedCode(uuKey+entry.uuid,true,$event)">
                            <nuxt-link :to="returnEmbedPath" style="color: inherit; text-decoration: inherit">embed</nuxt-link>
                        </div>
                        <!-- <div :id="'copy-embed-code'+uuKey+entry.uuid" class="col back-card-button" @click="copyEmbedCode(uuKey+entry.uuid,true,$event)">embed</div> -->
                    </div>

                    <b-popover
                        id="popover"
                        :target="'copy-link'+uuKey+entry.uuid"
                        :show.sync="showPopover"
                        triggers="click"
                        placement="top"
                    >
                        Copied to clipboard
                    </b-popover>

                    <!-- <b-popover
                        id="popover"
                        :target="'copy-embed-code'+uuKey+entry.uuid"
                        :show.sync="showEmbedPopover"
                        triggers="click"
                        placement="top"
                    >
                        Copied to clipboard
                    </b-popover> -->
                    
                </div>

              </div>
          </div>
      </div>
  </template>
  
  <script>
  import { copyToClipboard } from '~/utils/ClipboardUtils';

  export default {
    props: {
      uuKey: {
        default: 'no-key',
        type: String,
      },
      entry: {
        default: null,
        type: Object,
      },
      returnTo: {
        default: null,
        type: String,
      },
    },
    data(){
        return {
            showMoreOptions: false,
            showPopover: false,
            showEmbedPopover: false,
            showCheckIcon: false,
            showEmbedCheckIcon: false,
            intervalId: 0,
            previewBaseURL: this.$api.baseUrl,
            isFlipped:false,
            editorCode:''
        }
    },
    methods:{
        copyCode(key,hasPophover,ev) {
            clearInterval(this.intervalId);
            if(hasPophover){
                this.showPopover = true;
            }else{
                this.showCheckIcon = true;
            }
            this.intervalId = setInterval(
                function () {
                    this.showCheckIcon = false;
                    this.showPopover = false;
                }.bind(this),
                3000
            );
            
            let url = window.location.origin+this.returnPath;
            copyToClipboard(url);
        },
        copyEmbedCode(key,hasPophover,ev) {
            clearInterval(this.intervalId);
            if(hasPophover){
                this.showEmbedPopover = true;
            }else{
                this.showEmbedCheckIcon = true;
            }
            this.intervalId = setInterval(
                function () {
                    this.showEmbedCheckIcon = false;
                    this.showEmbedPopover = false;
                }.bind(this),
                3000
            );
            
            copyToClipboard(this.editorCode);
            console.log("this.editorCode",this.editorCode)
        },
        openMoreOptions(key,ev) {
            this.showMoreOptions = key;
        },
        collapseAllMoreOptions(e){
            if (!document.getElementById('more-options-zone'+this.showMoreOptions)) {
                return;
            }
            if (!document.getElementById('more-options-zone'+this.showMoreOptions).contains(e.target)) {
                this.showMoreOptions = false;
            }
        },
        toggleFlipped(){
            this.isFlipped = !this.isFlipped
            if(this.isFlipped){
                // load web comp
                this.loadPreview()
            }else{
                // unload web comp
            }
        },
        async loadPreview() {
            await this.$store.dispatch('webcomponent/loadWebcomponent', {
                uuid: this.entry.uuid,
                version: 'latest'
            });

            this.updatePreview(true)
        },
        updatePreview(force = false) {
            const src = this.externalPreviewUrl;
            if (!src) {
                return; // probably not yet loaded
            }
            
            if (!force && !this.autoUpdate) {
                return;
            }
            
            const oldElement = document.getElementById('tframe'+this.uuKey+this.entry.uuid);
            
            if (!oldElement) {
                return;
            }
            
            oldElement.parentNode.removeChild(oldElement);
            
            const newElement = document.createElement('iframe');
            newElement.setAttribute('id', 'tframe'+this.uuKey+this.entry.uuid);
            newElement.setAttribute('class', 'full-height full-width');
            newElement.setAttribute('style', 'min-height: 15rem;');
            newElement.setAttribute('frameborder', '0');

            document.getElementById('twrap'+this.uuKey+this.entry.uuid).appendChild(newElement);
            
            newElement.src = src;
            newElement.contentDocument.close();
        },
        resetEditorSnippet() {
            this.$store.dispatch('webcomponent/resetSnippet');
        }
    },
    mounted(){
        window.addEventListener('click', this.collapseAllMoreOptions);
    },
    unmounted() {
        if(this.isFlipped){
            this.resetEditorSnippet();
        }
    },

    watch: {
        snippet(value) {
            this.editorCode = value;
        },
    },
    computed: {
        snippet() {
            return this.$store.state.webcomponent.snippet;
        },
        config() {
            return this.$store.state.webcomponent.configuration;
        },  
        externalPreviewUrl() {
            if (!this.entry || !this.config) {
                return '';
            }
            return (
                this.previewBaseURL +
                '/preview/' +
                this.entry.uuid +
                '/latest?attribs=' +
                this.$store.getters['webcomponent/transportString']
            );
        },
        getLogo() {
            if (this.entry.image) {
                return (
                    this.$api.baseUrl + '/webcomponent/' + this.entry.uuid + '/logo/thumb'
                );
            }
    
            return '/component_placeholder.svg';
        },
        returnPath() {
            if (!this.entry) {
                return this.localePath('/');
            }
    
            if (this.returnTo === null || this.returnTo === '/') {
            return this.localePath({
                name: 'webcomponent-id',
                params: {
                id: this.entry.shortName ? this.entry.shortName : this.entry.uuid,
                },
            });
            }
    
            return this.localePath({
                name: 'webcomponent-id',
                params: {
                    id: this.entry.shortName ? this.entry.shortName : this.entry.uuid,
                },
                query: { from: this.returnTo },
            });
        },
        returnEmbedPath() {
            return this.returnPath+'#chooseRightSidebarTabEmbed'
        },
    },
  };
  </script>
  
  <style lang="scss">
  .mode-option-container{
    position: absolute;
    border-radius: 0.5rem;
    left: 1.2rem;
    top: 0;
    width: 10rem;
    font-size: 0.95rem;
    font-weight: 500;
    color: rgb(46, 49, 49);
    background-color: white;
    box-shadow: 0 0 5px rgb(46, 49, 49);
    overflow-y: none;

    .mode-option-icon{
        width: 1.3rem;
        margin-right: 0.3rem;
    }
  }

  /* The flip card container - set the width and height to whatever you want. We have added the border property to demonstrate that the flip itself goes out of the box on hover (remove perspective if you don't want the 3D effect */
  .flip-card {
    background-color: transparent;
    // width: 300px;
    height: 25rem;
    perspective: 1000px; /* Remove this if you don't want the 3D effect */
  }
  
  /* This container is needed to position the front and back side */
  .flip-card-inner {
    position: relative;
    width: 100%;
    height: 100%;
    text-align: center;
    transition: transform 0.8s;
    transform-style: preserve-3d;
  }

  /* Do an horizontal flip when you move the mouse over the flip box container */
    .flip-card.flipped{
        .flip-card-inner {
            transform: rotateY(180deg);
        }
    }

/* Do an horizontal flip when you move the mouse over the flip box container */
//   .flip-card:hover .flip-card-inner {
//     transform: rotateY(180deg);
//   }
  
  /* Position the front and back side */
  .flip-card-front, .flip-card-back {
    position: absolute;
    width: 100%;
    height: 100%;
    -webkit-backface-visibility: hidden; /* Safari */
    backface-visibility: hidden;
  }
  
  /* Style the front side (fallback if image is missing) */
  .flip-card-front {
    .overlay{
        position: absolute;
        z-index: 2;
        width: 100%;
        height: 100%;
        background: linear-gradient(180deg, rgba(0,0,0,0) 10%, rgba(46,49,49,1) 95%);
        overflow: hidden;
        border-radius: 0.4rem;
    }
    .aspect-box {
        height: 100%;
        top:0;
        position: relative;
        overflow: hidden;
        border-radius: 0.4rem;
        .aspect-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-size: cover;
        }
    }
    .front-content-container{
        color: white;
        width: 100%;
        z-index: 3;
        height: 8rem;
        position: absolute;
        bottom: 0;
        text-align: left;
        padding: 1rem;
        
        .title{
            width: 100%;
            height: auto;
            display: block;
            font-weight: bold;
            font-size: 1.2rem;
        }
        .categories{
            width: 100%;
            display: block;
            margin-top: -0.3rem;
            margin-bottom: 0.3rem;

            .category-badge{
                display: inline-block;
                background-color: white;
                border-radius: 0.2rem;
                font-size: 0.6rem;
                width: fit-content;
                color: rgb(46, 49, 49);
                font-weight: 500;
                padding: 0 0.3rem;
                margin: 0 0.1rem;
            }
        }
        .description{
            width: 100%;
            height: auto;
            display: block;
            font-weight: normal;
            font-size: 0.8rem;
            margin-top:0.5rem;
        }
    }
    .front-card-buttons-container{
        position: absolute;
        top: 55%;
        left: 0;
        margin: 0 auto;
        width: 100%;
        z-index: 2;

        .front-card-button{
            cursor: pointer;
            line-height: 17px;
            // border: 1px solid white;
            border-radius: 3px;
            text-transform: uppercase;
            padding: 0.7rem;
            margin: 0 0.6rem;
            width: fit-content;
            display: inline;
            font-family: "Source Sans Pro", "Open Sans", arial, sans-serif;
            font-weight: 600;
            font-size: 17px;
            background-color: white;
            color: #2e3131 !important;
            opacity:0.8;
            
            a{
                font-family: "Source Sans Pro", "Open Sans", arial, sans-serif;
                font-weight: 600;
                font-size: 17px;
                color: #2e3131 !important;
            }

            &:hover{
                opacity:1;
            }
        }

    }
  }
  
  /* Style the back side */
  .flip-card-back {
    background-color: white;
    color: rgb(46, 49, 49);
    transform: rotateY(180deg);
    overflow: hidden;
    border-radius: 0.4rem;

    .wc-command-bar{
        background-color: rgb(46, 49, 49,1);
        color: white;
        width: 100%;
        height: 10%;
    }
    .wc-iframe-container{
        width: 100%;
        height: 90%;

        .wc-iframe{
            width: 100%;
            height: 100%;
        }
    }
    
    .back-card-buttons-container{
        .back-card-button{
            cursor: pointer;
            padding: 0.49rem;
            border-right: 1px solid white;
            text-transform: uppercase;
            font-family: "Source Sans Pro", "Open Sans", arial, sans-serif;
            font-weight: 600;
            font-size: 17px;
            color: white;

            a{
                color: white !important;
            }

            &:last-child {
                border-right: none;
            }

        }
        
    }

  }
  
  
  
  </style>
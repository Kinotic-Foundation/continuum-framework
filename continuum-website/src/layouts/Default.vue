<template>
  <v-app>
    <v-app-bar absolute
               fixed
               app>

<!--        <v-app-bar-nav-icon></v-app-bar-nav-icon>-->

      <v-toolbar-title>Continuum</v-toolbar-title>

      <v-spacer></v-spacer>

      <v-btn text>
        <g-link to="/">Home</g-link>
      </v-btn>

      <v-btn text>
        <g-link to="/about">About us</g-link>
      </v-btn>

    </v-app-bar>

    <v-main>
      <v-container class="no-gutters" fill-height fluid>
        <v-row class="no-gutters fill-height">
          <v-col>
            <slot/>
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <v-footer app>
      <!-- -->
    </v-footer>
  </v-app>
</template>

<static-query>
query {
  metadata {
    siteName
  }
}
</static-query>

<style>


</style>

<template>
  <v-app>
    <v-app-bar color="white"
               app>

<!--        <v-app-bar-nav-icon></v-app-bar-nav-icon>-->

      <v-toolbar-title>Continuum</v-toolbar-title>

      <v-spacer></v-spacer>

      <v-btn text to="/">
        Home
      </v-btn>

      <v-btn text to="/about">
        About us
      </v-btn>

    </v-app-bar>

    <v-main>
      <v-container fill-height fluid>
        <slot/>
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

.v-application {
  font-family: 'Open Sans', Arial, sans-serif !important;
  font-size: 14px; color: #666 !important;
  background-color: #fff !important;
  font-weight: 500 !important;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

</style>

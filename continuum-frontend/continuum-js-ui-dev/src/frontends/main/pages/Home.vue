<template>
  <v-container class="no-gutters" fill-height fluid>
    <v-row class="fill-height">
      <v-col>
        <v-card>
          <v-card-text>
            <v-form
                ref="form"
                v-model="valid"
                lazy-validation>
              <v-text-field
                  v-model="name"
                  :counter="10"
                  :rules="nameRules"
                  label="Name"
                  required
              ></v-text-field>

              <v-text-field
                  v-model="email"
                  :rules="emailRules"
                  label="E-mail"
                  required
              ></v-text-field>

              <v-btn
                  :disabled="!valid"
                  color="success"
                  class="mr-4"
                  @click="submit"
              >
                Submit
              </v-btn>

              <v-btn
                  color="error"
                  class="mr-4"
                  @click="reset"
              >
                Reset Form
              </v-btn>

              <v-btn
                  color="warning"
                  @click="resetValidation"
              >
                Reset Validation
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator'

@Component({
             components: { }
           })
export default class Home extends Vue {

  private valid:boolean = true

  private name: string = ''

  private nameRules: any = [
    v => !!v || 'Name is required',
    v => (v && v.length <= 10) || 'Name must be less than 10 characters',
  ]

  private email: string = ''

  private emailRules: any = [
    v => !!v || 'E-mail is required',
    v => /.+@.+\..+/.test(v) || 'E-mail must be valid',
  ]

  constructor() {
    super()
  }


  // Lifecycle hooks
  public mounted() {
  }

  public beforeDestroy() {
  }

  public submit () {
    this.$refs.form.validate()
  }

  public reset () {
    this.$refs.form.reset()
  }

  public resetValidation () {
    this.$refs.form.resetValidation()
  }

}
</script>

<style scoped>

</style>

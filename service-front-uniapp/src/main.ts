import {createSSRApp} from "vue";
import App from "./App.vue";
// @ts-ignore
import uviewPlus from 'uview-plus'
import * as Pinia from "pinia";
// @ts-ignore
import {createUnistorage} from "pinia-plugin-unistorage";
import "@/api/interceptor"

export function createApp() {
    const app = createSSRApp(App);
    const store = Pinia.createPinia();
    store.use(createUnistorage());
    app.use(uviewPlus)
    app.use(store)
    return {
        app,
        Pinia
    };
}

import {useUserStore} from "@/stores/user";

const baseUrl = "http://localhost:9000";
const httpInterceptor = {
    /**
     * 拦截前触发
     */
    invoke(options: UniApp.RequestOptions) {
        //拼接地址
        if (!options.url.startsWith('http')) {
            options.url = baseUrl + options.url;
        }
        //设置超时时间
        options.timeout = 3000;
        //如果在外部定义，使用了let memberStore = useMemberStore(pinia)会导致持久化插件失效，切记切记切记
        let userStore = useUserStore()
        //添加token
        let token = userStore?.token;
        if (token) {
            //添加请求头标识
            options.header = {
                //保留原有的
                ...options.header,
                'token': token
            }
        }
    }
}
uni.addInterceptor('request', httpInterceptor);
uni.addInterceptor('uploadFile', httpInterceptor);


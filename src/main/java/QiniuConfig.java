import com.qiniu.util.Auth;

/**
 * Created by LiuQiulan
 *
 * @date 2018-7-20 13:53
 */
public class QiniuConfig {
    //设置好账号的ACCESS_KEY和SECRET_KEY
    private static String ACCESS_KEY_ceshi = "p-1liZqOtroJtCfVqpfgEeLvcp_XRQJi4YyredZz";
    private static String SECRET_KEY_ceshi = "4aTkvE4hwFqMt1psyL9iReJcRuA58imrBTcVECck";

    public static final String bucket_product = "product";
    public static final String Domain_product = "product.qiniu.doctornong.com";

    public static Auth returnAuth(){
        return Auth.create(ACCESS_KEY_ceshi, SECRET_KEY_ceshi);
    }
}

/**
 * Created by LiuQiulan
 *
 * @date 2018-7-25 15:43
 */
import java.io.File;
import java.io.FileInputStream;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.qiniu.util.Auth;
import com.qiniu.util.Base64;

public class Base64PictureDemo {
    public static void main(String[] args) throws Exception {
        putb64();
    }


    public static void putb64() throws Exception {

        String ACCESS_KEY = "ACCESS_KEY";
        String SECRET_KEY = "SECRET_KEY";
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);

        String token = auth.uploadToken("bucket", "key", 3600, null);

        FileInputStream fis = null;
        String file="path/file";

        try{
            //文件大小
            int l = (int)(new File(file).length());
            String url = "http://up.qiniu.com/putb64/" + l;
            byte[] src = new byte[l];

            //文件-输入流-字节数组-base64字符串
            fis = new FileInputStream(new File(file));
            fis.read(src);
            String file64 = Base64.encodeToString(src, Base64.DEFAULT);

            //构造post对象
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/octet-stream");
            post.addHeader("Authorization", "UpToken " + token);
            post.setEntity(new StringEntity(file64));

            //请求与响应
            HttpClient c = HttpClientBuilder.create().build();
            HttpResponse res = c.execute(post);

            //输出
            System.out.println(res.getStatusLine());
            String responseBody = EntityUtils.toString(res.getEntity(), "UTF-8");
            System.out.println(responseBody);

        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally{
            if(fis != null){
                fis.close();
            }
        }
    }

}
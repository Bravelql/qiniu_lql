/**
 * Created by LiuQiulan
 *
 * @date 2018-7-20 13:52
 */
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;

import java.io.*;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.StringMap;
import org.apache.commons.io.IOUtils;
import sun.nio.ch.IOUtil;

public class UploadDemo {
    Auth auth = QiniuConfig.returnAuth();

    //构造一个带指定Zone对象的配置类
    private static Configuration cfg = new Configuration(Zone.zone1());
    //创建上传对象
    UploadManager uploadManager = new UploadManager(cfg);
    //要上传的空间
    String bucketname = QiniuConfig.bucket_product;
    //上传到七牛后保存的文件名
    String key = "my-video20180720.mp4";
    //上传文件的路径
    String FilePath = "D:\\resuorce\\base64.txt";
    //String FilePath = "D:\\resuorce\\2.jpg";



    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken(){
        //有时候我们希望能自定义这个返回的JSON格式的内容，可以通过设置returnBody参数来实现，
        // 在returnBody中，我们可以使用七牛支持的魔法变量和自定义变量。
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize),\"persistentId\":$(persistentId)}");
        long expireSeconds = 3600;
        return auth.uploadToken(bucketname,null,expireSeconds,putPolicy);
    }

    public void upload() throws IOException{
        try {
            long start = System.currentTimeMillis();
            //调用put方法上传
            Response res = uploadManager.put(returnByte(FilePath), null, getUpToken());
            //打印返回的信息
            long end = System.currentTimeMillis();
            System.out.println("zijie 耗时："+(end-start)+"ms");
            //默认情况下，文件上传到七牛之后，在没有设置returnBody或者回调相关的参数情况下，
            // 七牛返回给上传端的回复格式为hash和key
            System.out.println(res.bodyString());
        } catch (QiniuException e) {
            Response r = e.response;
            // 请求失败时打印的异常的信息
            System.out.println(r.toString());
            try {
                //响应的文本信息
                System.out.println(r.bodyString());
            } catch (QiniuException e1) {
                //ignore
            }
        }
    }

    public byte[] returnByte(String filePath){
        //流化
        try {
            File f = new File(filePath);
            InputStream out = new FileInputStream(f);
            return IOUtils.toByteArray(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String args[]) throws IOException{
        new UploadDemo().upload();
    }

}

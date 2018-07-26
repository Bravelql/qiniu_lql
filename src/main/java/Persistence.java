/**
 * Created by LiuQiulan
 *
 * @date 2018-7-20 16:15
 */
/**
 * 常规的访问数据处理机制很适合像图片缩略图之类的访问，
 * 但无法应用于数据处理过程较长的资源，例如花费时间超过一分钟的音频转码，
 * 更不用说可能处理时间超过一小时的视频转码。
 *
 * 持久化数据处理(pfop)机制用于满足这种处理时间较长的场景。
 * 开发者可使用该功能对音视频进行异步转码，并将转码结果永久存储于空间中，
 * 从而大幅提升访问体验。持久化数据处理功能还提供即时的处理状态通知和查询功能，
 * 因此开发者在开始执行音视频转码后还能随时获取转码进度信息。
 *
 *
 */

/**
 * 持久化数据处理功能可以在以下两种场景触发：
     》在资源上传过程中，自动触发处理流程。
     》针对已存在空间中的资源，手动触发处理流程。
 */

import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.Base64;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

import java.io.IOException;

/**
 * 上传后自动触发
 */

/**
 * 结果查询接口
 * [GET] http://api.qiniu.com/status/get/prefop?id=z1.5b557360856db843bc2d5102

 */
public class Persistence {
    Auth auth = QiniuConfig.returnAuth();

    //构造一个带指定Zone对象的配置类
    private static Configuration cfg = new Configuration(Zone.zone1());
    //创建上传对象
    UploadManager uploadManager = new UploadManager(cfg);
    //要上传的空间
    String bucketname = QiniuConfig.bucket_product;
    //上传到七牛后保存的文件名
    String key = "my-video20180723.mp4";
    //上传文件的路径
    String FilePath = "D:\\resuorce\\video_20180720_094355.mp4";

    //简单上传，使用默认策略，只需要设置上传的空间名就可以了
    public String getUpToken(){
        //有时候我们希望能自定义这个返回的JSON格式的内容，可以通过设置returnBody参数来实现，
        // 在returnBody中，我们可以使用七牛支持的魔法变量和自定义变量。
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize),\"persistentId\":$(persistentId)}");

        //String fops = "avthumb/mp4/s/640x360/vb/1.25m";
        String fops = "avthumb/mp4/s/640x360/autoscale/1/vb/1.25m";//缩放比例
        //String fops = "avthumb/mp4/s/640x360/aspect/3:4/vb/1.25m";//播放比例
        //String fops = "vframe/jpg/offset/2/w/480/h/360";//抽图

        //设置转码操作参数

        //avthumb :七牛音视频转码接口
        // /mp4 :封装格式。
        // /s/640x360 : 指定视频分辨率，格式为<width>x<height>或者预定义值，width 取值范围 [20,3840]，height 取值范围 [20,2160]。
        // /vb/1.25m : 视频码率，单位：比特每秒（bit/s），常用视频码率：128k，1.25m，5m等。
        // 在不改变视频编码格式时，若指定码率大于原视频码率，则使用原视频码率进行转码。



        //可以对转码后的文件进行使用saveas参数自定义命名，当然也可以不指定文件会默认命名并保存在当前空间。
        String urlbase64 = UrlSafeBase64.encodeToString(bucketname+":"+key);
        System.out.println("key:"+key);
        String pfops = fops + "|saveas/"+urlbase64;

        putPolicy.put("persistentOps",fops);
        putPolicy.put("persistentNotifyUrl","http://liuqiulan.tunnel.qydev.com");
        long expireSeconds = 3600;
        return auth.uploadToken(bucketname,null,expireSeconds,putPolicy);
    }

    public void upload() throws IOException {
        try {
            long start = System.currentTimeMillis();
            //调用put方法上传
            Response res = uploadManager.put(FilePath, null, getUpToken());
            long end = System.currentTimeMillis();
            //打印返回的信息
            System.out.println("直传耗时："+(end-start)+"ms");

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

    public static void main(String args[]) throws IOException{
        new Persistence().upload();
    }

}

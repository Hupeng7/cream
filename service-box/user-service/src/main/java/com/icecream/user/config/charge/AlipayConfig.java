package com.icecream.user.config.charge;

/**
 * @author Mr_h
 * @version 1.0
 * description:
 * create by Mr_h on 2018/8/1 0001
 */
public class AlipayConfig {

    // 6.请求网关地址
    public static String URL = "https://openapi.alipay.com/gateway.do";

    public static String service = "mobile.securitypay.pay";//固定值
    //私钥
    public static String private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCp7Qi4SticzTLzykoQCbeMh+gKQd6u2NpIzlFE056A5E+tpaS5HLHrwoX+Miur3P6bRkk6sGGTE+hVWUZRVQMQPkzVXXq2ck/7OO5AHsqPVRIpMso7RmpAgtE6djPmmMY2o+IIOReQjq4pQK4yfb7ct9aT1HnDhC+YBdEXqEWXWvvlJNvfMtju+u6kmzU3qv4WUiDZRjKoU4RwT06UuAXzpd5jElZAFxk7SKYvQXFBYmbeACrtPVa4s3Ir/puBKT39J++ZK4swk+gExFBrY80iiobWklEZB1U5nNm000uM5tLTCT1QLQBnL6olB6sMD0IdfTcPHSD9kDQnwGEnDEG7AgMBAAECggEAUSxtbPQ8M/OGcpVg+fZaW2SVAUlkIixvYjZE9lcNMc9fDLnDTbaae+BJ+BtpZ0lDy1MxKjsV8Zeh9huWjsXIl9rbA1YLh9plwMH6dmD/LKdb7JhZqSVDHpwfbQkIVBEJJxL0qC71xdWS9xvx2ybcpT2g4K+wlyivP7UPpHh6/gjuHIXaca5fEq+mL5na5DaGDa/89Z1a5yx+SSXrbRPI3Iq6nxwz+nP7H5puPx0phA6lAEqiPNTew2UZHxgrbpe5bR0mqkPyxvxftyQXhfmb7QcKe3RwUt/9p1RnzcKxaKFibGnZa9hlq7ZlNrjMjrLgchfEd+Sin5so41rBkKn6+QKBgQDb/SjNxqCKUY3hO4MfubHWQlF+nj+d/wfRNVGHFqAzYYjBaQwZBbbIEWjAUnFkj/zFNdIGRhvrxLPYTP8rjNWvr0K21ujhW+rKX54t91FCYrEol0+zl12A93qCM9OISnQaFxh3C0so1VdwZK+C1md1kxsVxZRKkpvj8lSq6OpotwKBgQDFvfA07LHdTfrNnnlH62uPGl2vA7zkjA8zyQq+UWEIUOm6IAybv/DqeJD7abNVrgjvgNn43FqzmrbHpyujaixa0prR+kbPD786pSBCDJLwe8ZvHMUZS/yHISOdyNrwn5Y54GIxJ3yv82m+35KSZr0sNnO0x7TCubOSQDN4dh3DHQKBgHSV2gYT+gjT58kx7O/nTaQrSV09KKHnApGRHD/nccdJLVyy+0JXkOK+tzEVgBq3ZFJvj3rbtPhiKp27UJAX9zdAPq7/fjPQrsHJot7hbyMrgo/sgMTAt0Ed5sMSDEzyiE07aC/OwGpHhit+cLV6QyJAb94987UBtbQ8PTrGbVRJAoGAShLatpisEECz4O6qc/yGcDbqPTNjQSIOV9HJyn1lod3dkDGNR5LhRpQfi26PFVt6UW2tOYIiIAGm0qeWu0J8lxEpmdrtR4eYlPliWhunt5pGPT1DwDQsxtntI6AoE9dUSR366nrmRGskg7HwkBMYbkV/lorw9bjmwbR3E3r7rykCgYB2BBAVKUyel3Ial56dMNiB/DbEJ1FwOQwixCWPMkxgZYHhb7kryXNyriaiL/ucrliM3e8J9EdT3vcBNfBm2noW2t7Pz2FHS6ylWwylBBfyBADj0BIwWPReHsSIfGTO3QwOrMfqb8wY9ygnu/ve+vkZIsJHrg+4igyIIw5GJ1ofEg==";
    // 商户的公钥
    public static String public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkGIbxLFmqOCcGqgLOKfhgT+f7uKhKZhzTDp6gOHrlshkSU3ME7mpVEYGnKC0PtAkOxLBESYwDRtOKi7z1hv5kyTeUj5Zj1jgQwBrCZVFqOOMboOB/qb4vMepIsCHd0Wb+bD2kXazCSBFePCYjGH1O9cEBDDy3/8VHUeq/UpCe2U4nm8H6qZqBDl2eVLPWWIrBoGfi/R2ZPhp0eKm6d6Gr4FUV4OR0+dllIVbAxFLNZFcWC6aQAIKWXeOMfu/dyzCwqDQq5ZfvHxfv9aQXQLHzxMEKgD30ylKu0Xsm7DB9ZRCruZY4/1u1RR/dNWS0Pe/pPuwNUDOfoT8TDrMpinHFQIDAQAB";
    // 支付宝的公钥，无需修改该值（不要删除也不要修改，在接收通知的时候需要进行签名认证）
    public static String ali_public_key= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjhRu4mw0PkAGvsZQ8lOuDMSb6nBou6LX3Sw0U0WwOTBzLzda4HmqBR4bT6sGxfIOq3hWEF9pQb8d/QcLfoMYsT6GmG7Rpv0Nt1uOQ0hx2r8vt5tyhSJ3XKgXNUYyCMv3cj14kYeBeMmt5gaF16DYEO17gnzT1FwJ9bqwksFsvXWhpDTQKKHwkDqmjjbAtatBEWmbAcR/URHo+0zhx76PdHYBLoNPh2yPw2kF7l06D+CyGlDLIrrGdok5XkgVgmDdsMalNFUIm2I5FHazGXyKy++2FGheG+X8PoTGJyxSnHw001sereW4m32cBC58eCiwQwJdxHpxyAVhWqmqlRMDXwIDAQAB";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";
    // 签名方式 不需修改
    public static String sign_type = "RSA2";

    //APPID
    public static String APPID = "2018092661539460";
    //支付宝回调地址
    public static String notify_url = "http://html.adinnet.cn/zanzhushang.html";

    // 8.返回格式
    public static String FORMAT = "json";

}

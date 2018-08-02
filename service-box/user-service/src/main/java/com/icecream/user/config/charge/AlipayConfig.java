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
    public static String private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCQYhvEsWao4JwaqAs4p+GBP5/u4qEpmHNMOnqA4euWyGRJTcwTualURgacoLQ+0CQ7EsERJjANG04qLvPWG/mTJN5SPlmPWOBDAGsJlUWo44xug4H+pvi8x6kiwId3RZv5sPaRdrMJIEV48JiMYfU71wQEMPLf/xUdR6r9SkJ7ZTiebwfqpmoEOXZ5Us9ZYisGgZ+L9HZk+GnR4qbp3oavgVRXg5HT52WUhVsDEUs1kVxYLppAAgpZd44x+793LMLCoNCrll+8fF+/1pBdAsfPEwQqAPfTKUq7ReybsMH1lEKu5ljj/W7VFH901ZLQ97+k+7A1QM5+hPxMOsymKccVAgMBAAECggEATBgo4mVM4bxfCX1/TIDOTLwnolM29SEvwwEfB0YKUBGVKT0jTVIQeCXf3jSkSmaQccHUlyE1OfMmIv1T7YgY5QANW/MYaIBC0Y0q3IYbjINAxA3zOS7u8S+ZaINn4eiB5/roOIoCmdC0TKUhAPuvr7YGm97gYkWh7yiSaaP1nepUQgohcb0QxKiyaRYrrx8tYzsP9rIJLhqMm3ZRKuA7Wx2IbPoK5R9aVB8z4i/jhAJbZgKfFHHxHHchJEzzgxz5lYfty07pE1sVi4IJw86nEe3/H924fnBFCEqUpc+YeD7f/z2SRVaq9yugyDIytVZJlaTicAJ4GXU5uP5YTQdXgQKBgQDHHL7X0ArGQX6LjO19Mtct+yRYdxpJ2S0yVrUoOaA2z+JmNkhjiyCdm5eO8GqjDxzbLqnVjBW0x15BvDYOtDlTiQpEcPzPU8D1f8wtD3BripW9QAdlZzMBlsKo3u1ketyOyJs365u1UpwJwtiC5rJ/VIhMW78S5aqu5konxoMinQKBgQC5om4jgUksY9HmnIXlojzdiqoRnYJU3VqoTUrew8zHJbqYE4Qc40dkmamUcfeUdytezIBujmH9pm9Kxj9r69SJj/e0jd9Zi7bQKBbM67v2kyJm3AFHB11mskHElFK4WRLGnuRzZXmWit76izfUiTqs4Zzdg294NeHHhsnEe1Qw2QKBgQC5+//HVmy6AzNQ5rJu365fJNcuSxIjKNkuzA8rI6ijikrPbqTvVmWA0nUe7zKsXNF3an75GYCs/AzvGf7kfTOO89LDW0bJ4lG6/0SYUnOQAEMeI1DFR0A9m7T4SEM2OA0M0hUqhslK9X8LHxVeMF9K0Ir/yDMSU7S66iEaRjL3gQKBgHbPUACnaYbgqGIZwdT0HlKIwkqd7eGU/sYDGi0zUigPrLpSm1bF3Fa1xoR84MGD+B0nc/fOZ0cps8c+1S6kdJZKr3Y+6zlro2jcj6M+KUIqb3U30BV+0De/VTqU19CnKc43ue1lgAlq/kWKvwPnhMdLatOXoMtmaQgD67U2Xe8ZAoGBALhnXooU2SVaaxlAKw4X/JcweVfq/sSfblSgBUYRAWoBqZXCIz0fOzzkkThqG45BK+17gnrWWZOf5bIrM9E3qgm1xXD3xJ0QTQYYz4zKZRqFHUD0lrQ2bqYKYcrWYpaw0E/pwyX1v+GjdaZLloBCwcXUkJM6J6dzoMmZj0K15VBH";
    // 商户的公钥
    public static String public_key ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkGIbxLFmqOCcGqgLOKfhgT+f7uKhKZhzTDp6gOHrlshkSU3ME7mpVEYGnKC0PtAkOxLBESYwDRtOKi7z1hv5kyTeUj5Zj1jgQwBrCZVFqOOMboOB/qb4vMepIsCHd0Wb+bD2kXazCSBFePCYjGH1O9cEBDDy3/8VHUeq/UpCe2U4nm8H6qZqBDl2eVLPWWIrBoGfi/R2ZPhp0eKm6d6Gr4FUV4OR0+dllIVbAxFLNZFcWC6aQAIKWXeOMfu/dyzCwqDQq5ZfvHxfv9aQXQLHzxMEKgD30ylKu0Xsm7DB9ZRCruZY4/1u1RR/dNWS0Pe/pPuwNUDOfoT8TDrMpinHFQIDAQAB";
    // 支付宝的公钥，无需修改该值（不要删除也不要修改，在接收通知的时候需要进行签名认证）
    public static String ali_public_key= "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAupYE6SccqNg+/goE/hBc47Tbn06XmxLs2g/rAJOHMwhCi/tNbS4AtrkRMS7is0tSxxnU7iXqufAvmbNeUPOawbGQlPzSslMGh5I6Rvj1BiC0CRv3+9u+7n+Ig4Bi85j3f15k6VlHt+tzHVTqOmETLFdRvAwSSzlAsL28oL6Qy1sv/ivrJFlNRRXOlb5vEmmmDJhA7O59MLMbgA+YSn1P1+2lNhaQAkoQH+KA5tlmmQcVZXra2wsUc+0EgIXnOTMBgAXvAz0RUEzsR0Jsl3ryEhKugaKgpzaxnIy8RysPSxNh25A9vMSAd4olZZhrt9Jo/MpLD8enthRnsDCVl2sRlwIDAQAB ";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String input_charset = "utf-8";
    // 签名方式 不需修改
    public static String sign_type = "RSA";

    //APPID
    public static String APPID = "2017062207542763";
    //支付宝回调地址
    public static String notify_url = "http://html.adinnet.cn/zanzhushang.html";

    // 8.返回格式
    public static String FORMAT = "json";

}

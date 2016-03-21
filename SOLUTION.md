TITLE: spring-boot で response time/status code/response bytes などの統計値を効率よく取得したい
FORMAT: mkdn

# 今回、解決したい問題
 
 * nginx/apache を前段に置かないので、service の status code の割合の監視を jetty から直接行いたい
 * request の content-length, response time の統計値なども monitoring システムに投げたい

# MetricsFilter について

spring boot actuator ではデフォルトで MetricsFilter という servlet filter が設定されている。
これにより、各エンドポイントの HTTP status の stats が取得されている。

```
{
counter.status.200.star-star.favicon.ico: 5,
counter.status.200.metrics: 5,
counter.status.200.wow.hello: 1,
counter.status.200.hello: 1
...
}
```

`/**/favicon.ico` のようなパスが '.star-star.favicon.ico' のような名前に変換されているのは、graphite との互換性のためとのこと。

その他、レスポンスタイムも記録されているが、gauge の値がたんに保存されるだけで、各 endpoint ごとに過去一回の値がとれるだけになっている。
middleware に全部の値を投げるように設定して、集計すればいいのかもしれないが、ちょっと面倒なようにも感じる。
そして、何かリクエストがあるたびにソケット通信して外部ミドルウェアに投げるのも微妙。

# 解決策

dropwizard metrics を利用して、histogram で処理するようにすべし。
ついでに、response bytes とかも取れる様になってるといいな、的な。

そういうわけで、そういったことをいい感じにしてくれるライブラリを書いた。
build.gradle に以下のように依存として記述すべし。

    compile 'me.geso:spring-boot-richmetrics:0.0.1'

これだけで、なんとなく metrics 値が取得されるようになります。


TITLE: spring boot アプリケーションの実行時メトリクス値取得について
FORMAT: mkdn

# Spring boot の metrics 機能について

spring boot には metrics 機能が標準でついている。metrics では Counter と Gauge の2種類のメトリクスを食わせることが可能である。

Counter は、単純に増加/現象する値を表すのに使うメトリクス値である。
たとえば、アクセスの件数、エラーの件数、現在利用中の値を取得したりするのに使う。

サンプルコードは以下の通り。

    @Service
    public class FooService {
        @AutoWired
        private CounterService counterService;
        @Autowired
        private FooRepository fooRepository;

        public Foo get(String key) {
            counterService.increment("foo");
            return fooRepository.get(key);
        }
    }

Gauge は、単純に数値を渡すのに使う。普通に使うと、直近の一個の値がそのまま metrics 数字を取得できるだけなので、利用方法がちょっと思いつかない。
サンプルコードとしては以下の様な感じで利用される。

    gaugeService.set("foo", 3.14);

## Metrics 値の利用

### Actuator での閲覧

特に設定せずに spring boot アプリケーションを起動すると、`http://localhost:8080/metrics` からアプリケーションのメトリクス値が取得可能になっている。

ここを閲覧して、変な値が出ていないか確認することができる。
ここを cron で取得して、alert を上げるなどの利用法が行われている。と思う。

### 外部レポジトリへの送信

spring-boot の metrics では、データを外部のミドルウェアに送信することができる。

 * graphana
 * open tsdb
 * redis

gauge の値は actuator で確認するぶんには、最後の一個が見れるだけなので、使いにくいが、各値を外部 middleware に保存することもできる。

## Dropwizard Metrics との統合

dropwizard という web application framework には metrics という似たようなしくみがあるのだが、dropwizard の metrics の方がよく作りこまれている。。んですよ。
で、spring-boot の metrics は、classpath に dropwizard metrics があれば、そっちを使うように実装が差し替わります。

Dropwizard Metrics を利用するようにすると、gauge を取得するときに、prefix に 'histogram.' あるいは 'timer.' をつけると、生の値ではなく、min/max/stddev/percentile などの値が取得できるようになります。

Dropwizard metrics では、高速にメトリクス集計することが可能。Reservoir というコンポーネントで、集計を行っている。
Reservoir は性質のことなるによって幾つかの実装が存在している。

### Uniform Reservoirs

Uniform Reservoirs は、Vitter's Algorithm R ってやつでランダムサンプリングして集計している。
長期間の間のメトリクスを取るのに向いてる。データの傾向が変わったことを検出するのとかには向いてない。

### Exponentially Decaying Reservoirs

http://dimacs.rutgers.edu/~graham/pubs/papers/fwddecay.pdf
詳しくはこのへん。

過去、約５分間の傾向をしる事ができる。メトリクス傾向の変化を即座に検知したいなんてときにはこれ。
spring-boot-actuator の histogram 実装はこれを*デフォルト*として選択している。
とりあえずこれをそのまま使っておけばいい。と思う。

### Sliding Window Reservoirs

過去N個のデータを利用して値を出す

### Sliding Time Window Reservoirs

過去N秒のデータを利用して結果を出す。データの流量が多いとメモリ量めっちゃ食って死ぬ。


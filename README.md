# spring-boot-richmetrics-filter

Get web application's *Rich* metrics value without configuration.

## How it works?

spring-boot-richmetrics-filter provides servlet filter to get following metrics values.

 * Elapsed time during request
 * Response bytes
 * HTTP status code(This is available by MetricsFilter in spring-boot core)

## Sample output

```
{
   "classes" : 5907,
   "classes.loaded" : 5907,
   "classes.unloaded" : 0,
   "counter.status.200.metrics" : 12,
   "counter.status.200.star-star.favicon.ico" : 2,
   "gauge.response.metrics" : 6,
   "gauge.response.star-star.favicon.ico" : 3,
   "gc.ps_marksweep.count" : 1,
   "gc.ps_marksweep.time" : 64,
   "gc.ps_scavenge.count" : 6,
   "gc.ps_scavenge.time" : 110,
   "heap" : 1864192,
   "heap.committed" : 286720,
   "heap.init" : 131072,
   "heap.used" : 138748,
   "histogram.request.content_length.count" : 2,
   "histogram.request.content_length.snapshot.75thPercentile" : 946,
   "histogram.request.content_length.snapshot.95thPercentile" : 946,
   "histogram.request.content_length.snapshot.98thPercentile" : 946,
   "histogram.request.content_length.snapshot.999thPercentile" : 946,
   "histogram.request.content_length.snapshot.99thPercentile" : 946,
   "histogram.request.content_length.snapshot.max" : 946,
   "histogram.request.content_length.snapshot.mean" : 946,
   "histogram.request.content_length.snapshot.median" : 946,
   "histogram.request.content_length.snapshot.min" : 946,
   "histogram.request.content_length.snapshot.stdDev" : 0,
   "histogram.request.elapsed.count" : 14,
   "histogram.request.elapsed.snapshot.75thPercentile" : 7,
   "histogram.request.elapsed.snapshot.95thPercentile" : 25,
   "histogram.request.elapsed.snapshot.98thPercentile" : 148,
   "histogram.request.elapsed.snapshot.999thPercentile" : 148,
   "histogram.request.elapsed.snapshot.99thPercentile" : 148,
   "histogram.request.elapsed.snapshot.max" : 148,
   "histogram.request.elapsed.snapshot.mean" : 11.7375008843739,
   "histogram.request.elapsed.snapshot.median" : 6,
   "histogram.request.elapsed.snapshot.min" : 3,
   "histogram.request.elapsed.snapshot.stdDev" : 26.6402051866509,
   "httpsessions.active" : 0,
   "httpsessions.max" : -1,
   "instance.uptime" : 104678,
   "mem" : 334217,
   "mem.free" : 147971,
   "meter.status.200.count" : 14,
   "meter.status.200.fifteenMinuteRate" : 0.734473361236399,
   "meter.status.200.fiveMinuteRate" : 0.621702669526319,
   "meter.status.200.meanRate" : 0.145272889147657,
   "meter.status.200.oneMinuteRate" : 0.268911139809961,
   "nonheap" : 0,
   "nonheap.committed" : 48704,
   "nonheap.init" : 2496,
   "nonheap.used" : 47498,
   "processors" : 4,
   "systemload.average" : 3.33056640625,
   "threads" : 21,
   "threads.daemon" : 19,
   "threads.peak" : 21,
   "threads.totalStarted" : 25,
   "uptime" : 112276
}
```

## Configuration

You can set the properties like this for special paths:

    richmetrics.patterns.hello_id=/hello/*

Then, RichMetricsFilter take metrics about requests for `/hello/*`.

## Why don't you use MetricsFilter?
MetricsFilter don't provide metrics data such as above.

## LICENSE

    The MIT License (MIT)
    Copyright © 2016 Tokuhiro Matsuno, http://64p.org/ <tokuhirom@gmail.com>
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the “Software”), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.

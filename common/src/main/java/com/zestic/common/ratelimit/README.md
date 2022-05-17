#Rate limiting / throttling

Different scenarios require different throttling algorithms. How can you choose the right throttling algorithm for your scenario?.
In this project we are trying to build different throttling / rate limiting algorithms, such as simple window, sliding window, leaky bucket, token bucket etc...

##What is Throttling / Rate Limiting
Throttling is crucial. Throttling is used to protect limited downstream resources from traffic bursts and assure service availability in most cases. Throttling thresholds are often adjustable, allowing the limit to be raised as needed.

The throttling service is priced in some cases. Some cloud companies, for example, bill customers for API calls. The number of calls normally cannot surpass the threshold because money is involved.

##Token Bucket
A token bucket implementation that is of a leaky bucket in the sense that it has a finite capacity and any added tokens that would exceed this capacity will overflow out of the bucket and are lost forever.


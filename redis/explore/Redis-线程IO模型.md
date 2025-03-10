## Redis-线程IO模型

### Redis单线程为什么还能这么快？

* 因为所有数据都在内存中，所有的运算都是内存级别的运算。
* Redis是单线程，要小心使用Redis指令，对于时间复杂度为O(n)级别的指令，一定要谨慎使用，一不小心就可能会导致Redis卡顿。

### Redis单线程如何处理那么多的并发客户端连接？

* 

### 非阻塞IO

* 非阻塞IO在套接字对象上提供了一个选项Non_Blocking，当这个选项打开时，读写方法不会阻塞。能读多少取决于内核为套接字分配的读缓冲区内部的数据字节数，能写多少取决于内核为套接字分配的写缓冲区的空闲空间字节数。
* 有了非阻塞IO意味着线程在读写IO时可以不必再阻塞了，读写可以瞬间完成然后线程可以继续干别的事了。

### 事件轮询（多路复用）

![image-20210830204838324](https://cdn.jsdelivr.net/gh/ClareTung/ImageHostingService/img/image-20210830204838324.png)

* 事件轮询API是`select`函数，它是操作系统提供给用户程序的API。
* 输入是读写描述符列表 read_fds & write_fds，输出是与之 对应的可读可写事件。同时还提供了一个 timeout 参数，如果没有任何事件到来，那么就最多 等待 timeout 时间，线程处于阻塞状态。一旦期间有任何事件到来，就可以立即返回。时间过 了之后还是没有任何事件到来，也会立即返回。拿到事件后，线程就可以继续挨个处理相应 的事件。处理完了继续过来轮询。于是线程就进入了一个死循环，我们把这个死循环称为事 件循环，一个循环为一个周期。
* 每个客户端套接字 socket 都有对应的读写文件描述符。
* 现代操作系统的多路复用 API 已经不再使用 select 系统调用，而 改用 epoll(linux)和 kqueue(freebsd & macosx)，因为 select 系统调用的性能在描述符特别多时性能会非常差。

### 指令队列

* Redis 会将每个客户端套接字都关联一个指令队列。客户端的指令通过队列来排队进行 顺序处理，先到先服务。

### 响应队列

* Redis 同样也会为每个客户端套接字关联一个响应队列。
* Redis 服务器通过响应队列来将 指令的返回结果回复给客户端。 

### 定时任务

* Redis 的定时任务会记录在一个称为最小堆的数据结构中。这个堆中，最快要执行的任 务排在堆的最上方。在每个循环周期，Redis 都会将最小堆里面已经到点的任务立即进行处 理。处理完毕后，将最快要执行的任务还需要的时间记录下来，这个时间就是 select 系统调 用的 timeout 参数。因为 Redis 知道未来 timeout 时间内，没有其它定时任务需要处理，所以 可以安心睡眠 timeout 的时间。


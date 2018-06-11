 _      _____ _____ _____ ___  _ ____  ____  ____ 
/ \  /|/  __//__ __Y__ __\\  \///  __\/  __\/   _\
| |\ |||  \    / \   / \   \  / |  \/||  \/||  /  
| | \|||  /_   | |   | |   / /  |    /|  __/|  \_ 
\_/  \|\____\  \_/   \_/  /_/   \_/\_\\_/   \____/


##    ## ######## ######## ######## ##    ## ########  ########   ######  
###   ## ##          ##       ##     ##  ##  ##     ## ##     ## ##    ## 
####  ## ##          ##       ##      ####   ##     ## ##     ## ##       
## ## ## ######      ##       ##       ##    ########  ########  ##       
##  #### ##          ##       ##       ##    ##   ##   ##        ##       
##   ### ##          ##       ##       ##    ##    ##  ##        ##    ## 
##    ## ########    ##       ##       ##    ##     ## ##         ######  

RpcServerStarter.main->NettyRpcRegisteryParser.parse->NettyRpcRegistery.afterPropertiesSet->MessageRecvExecutor.start->
MessageRecvChannelInitializer->MessageRecvChannelInitializer.buildRpcSerializeProtocol

收到消息
MessageRecvChannelInitializer.initChannel->RpcRecvSerializeFrame.select->ProtostuffRecvHandler.handle
MessageRecvChannelInitializer.initChannel->RpcRecvSerializeFrame.select->ProtostuffRecvHandler.handle


MessageRecvChannelInitializer.initChannel->RpcRecvSerializeFrame.select->ProtostuffRecvHandler.handle
ProtostuffCodecUtil.decode->MessageRecvHandler.channelRead->ProtostuffCodecUtil.encode
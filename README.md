TODO:
more unit testing
2 unhandled error cases  429 and timeout from downstream service
move stuff to properties - you have some hardcoded values

if backend isnt up for frontend then currently throws 500: Http failure response for http://localhost:4200/api/games/0ow95tBD/solve: 500 Internal Server Error
5:14:05 PM [vite] http proxy error: /api/games/0ow95tBD/solve
AggregateError [ECONNREFUSED]:                                                                                                                                                    
at internalConnectMultiple (node:net:1193:18)                                                                                                                                 
at afterConnectMultiple (node:net:1783:7)                                                                                                                                     
5:15:33 PM [vite] http proxy error: /api/games/0ow95tBD/state
AggregateError [ECONNREFUSED]:                                                                                                                                                    
at internalConnectMultiple (node:net:1193:18)                                                                                                                                 
at afterConnectMultiple (node:net:1783:7)                                                                                                                                     
5:15:34 PM [vite] http proxy error: /api/strategies
AggregateError [ECONNREFUSED]:                                                                                                                                                    
at internalConnectMultiple (node:net:1193:18)                                                                                                                                 
at afterConnectMultiple (node:net:1783:7)                                                                                                                                     
5:15:35 PM [vite] http proxy error: /api/games/0ow95tBD/state
AggregateError [ECONNREFUSED]:                                                                                                                                                    
at internalConnectMultiple (node:net:1193:18)                     
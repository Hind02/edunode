import { HttpInterceptorFn } from '@angular/common/http';

export const apiKeyInterceptor: HttpInterceptorFn = (req, next) => {
  // Only add API key for mutating methods
  if (['POST', 'PUT', 'DELETE'].includes(req.method)) {
    const authReq = req.clone({
      setHeaders: {
        'X-API-KEY': 'edunode-admin-key' // Simple API Key from requirements
      }
    });
    return next(authReq);
  }
  
  return next(req);
};

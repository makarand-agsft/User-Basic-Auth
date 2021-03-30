import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Observable } from 'rxjs';
import { environment } from './environments/environment';



@Injectable({
  providedIn: 'root'
})
export class HttpService {

  constructor(public http: HttpClient) { }
  // return this.http.get('http://192.168.50.85:8080/palm-insurance/hii');

  generatePDF(formPolicyDTOObject): Observable<any> {
    return this.http.post(environment.generatePDF, formPolicyDTOObject, {
     observe: 'response'
    });
  }
  checkStatusAndDownloadPDF(requestId): Observable<any> {
    const httpOptions = {};
    httpOptions['responseType'] = 'blob';
    httpOptions
    return this.http.get(environment.downloadPDF + requestId, {
      responseType: 'blob',observe: 'response'
    });
  }
}

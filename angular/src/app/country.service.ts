import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

// @Injectable({
//   providedIn: 'root'
// })
// export class CountryService {
//
//   private countriesUrl = 'http://localhost:8080/countries'; // URL эндпоинта для получения списка стран с бэкенда
//
//   constructor(private httpClient: HttpClient) { }
//
//   getCountries(): Observable<Country[]>{
//     return this.get<Country[]>(this.countriesUrl)
//   }
// }

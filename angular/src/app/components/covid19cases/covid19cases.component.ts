import {Component, NgZone, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {DatePipe} from "@angular/common";


interface CountryData {
  country: string;
}

// interface MaxMinCases{
//   country: string;
//   maxCases: string;
//   maxCasesDate: Date;
//   minCases: string;
//   minCasesDate: string;
// }

interface MaxMinCases {
  country: string;
  maxCases: string;
  dateMaxCases: string;
  minCases: string;
  dateMinCases: string;
}

@Component({
  selector: 'app-xyz',
  templateUrl: './covid19cases.component.html',
  styleUrls: ['./covid19cases.component.css'],

})

export class Covid19casesComponent implements OnInit{

  private countryUrl = 'http://localhost:8080/covid19/countries';

  public countryList: string[] = [];
  public selectedCountry: string = '';
  public startDate: string = '';
  public endDate: string = '';
  public maxCases: string = '';
  public minCases: string = '';

  public dateMaxCases: string = '';
  public dateMinCases: string = '';

  constructor(private httpClient: HttpClient) { }

  ngOnInit(): void {
    this.httpClient.get<CountryData[]>(this.countryUrl).subscribe({

      next: ((response: CountryData[])  => {

        response.forEach(data => {
          if (!this.countryList.includes(data.country)) {
            this.countryList.push(data.country);
          }
          this.countryList.sort()
        });
      }),

      error: (error => {
        console.log(error);
      })

    })
  }

  public getMaxMinCases(): void{
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type':  'application/json',
        'Authorization': 'my-auth-token',
        'Access-Control-Allow-Origin': 'http://mydomain.com'
      })
    };
  const maxMinUrl = `http://localhost:8080/covid19/maxMin/country/${this.selectedCountry}?from=${this.startDate}&to=${this.endDate}`;
    this.httpClient.get<MaxMinCases>(maxMinUrl, httpOptions).subscribe({
      next: ((response: MaxMinCases)  => {
        this.maxCases = response.maxCases;
        this.minCases = response.minCases;

        const dateMax = new Date(Date.parse(response.dateMaxCases));
        this.dateMaxCases = `${dateMax.getDate()}/${dateMax.getMonth() + 1}/${dateMax.getFullYear()}`.toString();
        const dateMin = new Date(Date.parse(response.dateMinCases));
        this.dateMinCases = `${dateMin.getDate()}/${dateMin.getMonth() + 1}/${dateMin.getFullYear()}`.toString();

      }),

      error: (error => {
        console.log(error);
      })
    });
  }
}

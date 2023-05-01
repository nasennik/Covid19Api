import { NgModule } from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {Covid19casesComponent} from "./components/covid19cases/covid19cases.component";


//связывание адреса и компонентаб куда запрос будет перенаправлен
const routes: Routes = [

  {path: '', component: Covid19casesComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }

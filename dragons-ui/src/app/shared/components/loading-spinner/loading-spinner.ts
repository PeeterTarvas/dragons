import { Component } from '@angular/core';
import {TranslocoPipe} from '@ngneat/transloco';

@Component({
  selector: 'app-loading-spinner',
  imports: [TranslocoPipe],
  templateUrl: './loading-spinner.html',
  styleUrl: './loading-spinner.css',
})
export class LoadingSpinner {}

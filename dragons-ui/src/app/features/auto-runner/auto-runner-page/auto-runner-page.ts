import { Component } from '@angular/core';
import {RouterLink} from '@angular/router';
import {TranslocoPipe} from '@ngneat/transloco';

@Component({
  selector: 'app-auto-runner-page',
  imports: [
    RouterLink,
    TranslocoPipe
  ],
  templateUrl: './auto-runner-page.html',
  styleUrl: './auto-runner-page.css',
})
export class AutoRunnerPage {}

import {Component, input} from '@angular/core';

@Component({
  selector: 'app-stat-badge',
  templateUrl: './stat-badge.html'
})
export class StatBadge {
  label = input.required<string>();
  value = input.required<number | string>();
}

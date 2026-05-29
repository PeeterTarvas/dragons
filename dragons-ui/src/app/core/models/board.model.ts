import {Message} from './message.model';

export interface Board {
  ads: Message[];
  recommendedAdId: string | null;
}

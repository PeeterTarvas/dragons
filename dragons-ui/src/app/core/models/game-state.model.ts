import {TurnLog} from './turn-log.model';

export interface GameState {
  gameId: string;
  lives: number;
  gold: number;
  level: number;
  score: number;
  turn: number;
  reachedGoal: boolean;
}

import {TurnLog} from './turn-log.model';
import {GameState} from './game-state.model';

export interface GameResult {
  gameStateDto: GameState;
  log: TurnLog[]
}

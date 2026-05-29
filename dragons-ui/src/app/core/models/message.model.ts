export interface Message {
  adId: string;
  message: string;
  reward: number;
  expiresIn: number;
  probability: string;
  encrypted: number;
  estimatedSuccess: number;
}

import { Nutritionist } from './definitions';
import { authGet } from './auth';

export async function fetchActiveNutritionists(): Promise<Nutritionist[]> {
  try {
    return await authGet<Nutritionist[]>('/nutritionists');
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Failed to fetch nutritionist data');
  }
}
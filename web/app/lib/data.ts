export async function fetchActivePatients() {
  try {
    const data = await fetch("http://localhost:8080/api/patients");
    const patients = await data.json();
    return patients;
  } catch (err) {
    console.error('API error: ', err);
    throw new Error('Failed to fetch patient data');
  }
}

export async function fetchActiveNutritionists() {
  try {
    const data = await fetch("http://localhost:8080/api/nutritionists/active");
    const patients = await data.json();
    return patients;
  } catch (err) {
    console.error('API error: ', err);
    throw new Error('Failed to fetch nutritionist data');
  }
}
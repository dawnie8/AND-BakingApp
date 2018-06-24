package ipomoea.bakingapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import ipomoea.bakingapp.model.Ingredient;
import ipomoea.bakingapp.model.Recipe;
import ipomoea.bakingapp.model.Step;

class JsonHelper {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String INGREDIENTS = "ingredients";
    private static final String QUANTITY = "quantity";
    private static final String MEASURE = "measure";
    private static final String INGREDIENT = "ingredient";
    private static final String STEPS = "steps";
    private static final String SHORT_DESCRIPTION = "shortDescription";
    private static final String DESCRIPTION = "description";
    private static final String VIDEO_URL = "videoURL";
    private static final String THUMBNAIL_URL = "thumbnailURL";
    private static final String SERVINGS = "servings";
    private static final String IMAGE = "image";

    static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    static List<Recipe> extractFeatureFromJson(String recipeJSON) {

        List<Recipe> recipes = new ArrayList<>();

        try {
            JSONArray recipesJsonArray = new JSONArray(recipeJSON);

            for (int i = 0; i < recipesJsonArray.length(); i++) {

                JSONObject recipesJsonObject = recipesJsonArray.getJSONObject(i);

                int id = -1;
                if (recipesJsonObject.has(ID)) {
                    id = recipesJsonObject.optInt(ID);
                }

                String name = "";
                if (recipesJsonObject.has(NAME)) {
                    name = recipesJsonObject.optString(NAME);
                }

                List<Ingredient> ingredients = new ArrayList<>();
                if (recipesJsonObject.has(INGREDIENTS)) {
                    JSONArray ingredientsJsonArray = recipesJsonObject.getJSONArray(INGREDIENTS);
                    for (int j = 0; j < ingredientsJsonArray.length(); j++) {
                        JSONObject ingredientsJsonObject = ingredientsJsonArray.getJSONObject(j);
                        String quantity = "";
                        if (ingredientsJsonObject.has(QUANTITY)) {
                            quantity = ingredientsJsonObject.optString(QUANTITY);
                        }
                        String measure = "";
                        if (ingredientsJsonObject.has(MEASURE)) {
                            measure = ingredientsJsonObject.optString(MEASURE);
                        }
                        String ingredient = "";
                        if (ingredientsJsonObject.has(INGREDIENT)) {
                            ingredient = ingredientsJsonObject.optString(INGREDIENT);
                        }

                        Ingredient ingredientObj = new Ingredient(quantity, measure, ingredient);
                        ingredients.add(ingredientObj);
                    }
                }

                List<Step> steps = new ArrayList<>();
                if (recipesJsonObject.has(STEPS)) {
                    JSONArray stepsJsonArray = recipesJsonObject.getJSONArray(STEPS);
                    for (int y = 0; y < stepsJsonArray.length(); y++) {
                        JSONObject stepsJsonObject = stepsJsonArray.getJSONObject(y);
                        int stepId = -1;
                        if (stepsJsonObject.has(ID)) {
                            stepId = stepsJsonObject.optInt(ID);
                        }
                        String shortDescription = "";
                        if (stepsJsonObject.has(SHORT_DESCRIPTION)) {
                            shortDescription = stepsJsonObject.optString(SHORT_DESCRIPTION);
                        }
                        String description = "";
                        if (stepsJsonObject.has(DESCRIPTION)) {
                            description = stepsJsonObject.optString(DESCRIPTION);
                        }
                        String videoUrl = "";
                        if (stepsJsonObject.has(VIDEO_URL)) {
                            videoUrl = stepsJsonObject.optString(VIDEO_URL);
                        }
                        String thumbnailUrl = "";
                        if (stepsJsonObject.has(THUMBNAIL_URL)) {
                            thumbnailUrl = stepsJsonObject.optString(THUMBNAIL_URL);
                        }

                        Step stepObj = new Step(stepId, shortDescription, description, videoUrl, thumbnailUrl);
                        steps.add(stepObj);
                    }
                }

                String servings = "";
                if (recipesJsonObject.has(SERVINGS)) {
                    servings = recipesJsonObject.optString(SERVINGS);
                }

                String image = "";
                if (recipesJsonObject.has(IMAGE)) {
                    image = recipesJsonObject.optString(IMAGE);
                }

                Recipe recipeObj = new Recipe(id, name, ingredients, steps, servings, image);
                recipes.add(recipeObj);

            }

        } catch (JSONException e) {
            Log.e("JsonHelper", "Problem with JSON parsing", e);
        }

        return recipes;
    }
}
package system.recommendation;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DatasetLoader {

    private final Map<Integer, String> moviesNames = new HashMap<>();
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Movie> movies = new HashMap<>();




    public DatasetLoader(String datasetFolderName) throws FileNotFoundException {
        System.out.println("Loading data from " + datasetFolderName);
        setMoviesNames(datasetFolderName);
        handleRatings(datasetFolderName);
        System.out.println("Finished loading data from " + datasetFolderName);
    }

    private void handleRatings(String datasetFolderName) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(datasetFolderName + "/ratings.csv"))){
            scanner.nextLine();
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] data = line.split(",");
                int userId = Integer.parseInt(data[0]);
                int movieId = Integer.parseInt(data[1]);
                double rating = Double.parseDouble(data[2]);

                User user;
                if(!users.containsKey(userId)){
                    user = new User();
                    users.put(userId, user);
                }else{
                    user = users.get(userId);
                }
                user.addRating(movieId, rating);

                if(!movies.containsKey(movieId)){
                    Movie movie = new Movie(movieId);
                    movie.addUser(user);
                    movies.put(movieId, movie);
                }
            }
        }
    }

    private void setMoviesNames(String datasetFolderName) throws FileNotFoundException{
        try (Scanner scanner = new Scanner(new File(datasetFolderName + "/movies.csv"))){
            scanner.nextLine();
            while(scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] data = line.split(",");
                moviesNames.put(Integer.parseInt(data[0]), data[1]);
            }
        }
    }
}

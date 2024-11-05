package system.recommendation;

import system.recommendation.models.Movie;
import system.recommendation.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DatasetLoader {

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, Movie> movies = new HashMap<>();
    private final Map<Integer, Integer> moviesFakeRealID = new HashMap<>();
    private final Map<String, Integer> tags = new HashMap<>();

    private final Map<Double,Byte> ratingMapping = Map.of(
            1.0,(byte)1,
            1.5,(byte)2,
            2.0,(byte)3,
            2.5,(byte)4,
            3.0,(byte)5,
            3.5,(byte)6,
            4.0,(byte)7,
            4.5,(byte)8,
            5.0,(byte)9
            );

    private final boolean divideData;
    private final double testSize = 0.20;
    private final Random random = new Random();

    public DatasetLoader(String datasetFolderName, boolean divideData) throws FileNotFoundException {
        this.divideData = divideData;

        System.out.println("Loading data from " + datasetFolderName);
        addMovies(datasetFolderName);
        addTags(datasetFolderName);
        handleRatings(datasetFolderName);
        System.out.println("Finished loading data from " + datasetFolderName);

        moviesFakeRealID.clear();
    }

    public Map<String,Integer> getTags(){
        return this.tags;
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

    public Map<Integer, Movie> getMovies() {
        return movies;
    }

    private void handleRatings(String datasetFolderName) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(datasetFolderName + "/ratings.csv"))){
            scanner.nextLine();
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] data = line.split(",");
                int userId = Integer.parseInt(data[0]);
                int movieFakeId = Integer.parseInt(data[1]);
                double rating = Double.parseDouble(data[2]);

                User user;
                Integer movieId = moviesFakeRealID.get(movieFakeId);
                Movie movie = movies.get(movieId);

                if(!users.containsKey(userId)){
                    user = new User(userId);
                    users.put(userId, user);
                    if(!divideData)user.setTestRatings(user.getRatings());
                }else{
                    user = users.get(userId);
                }

                if(divideData && random.nextDouble() < testSize){
                    user.addTestRating(movieId, rating);
                }else{
                    user.addRating(movieId, rating);
                }
            }
        }
    }

    private void addMovies(String datasetFolderName) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(datasetFolderName + "/movies.csv"))){
            scanner.nextLine();
            int ctr = 1;
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] data = line.split(",");
                int movieId = Integer.parseInt(data[0]);
                moviesFakeRealID.put(movieId, ctr);
                String[] genres = data[2].split("\\|");
                Movie movie = new Movie(ctr);
                for(String genre : genres){
                    movie.addGenre(genre);
                }
                movies.put(ctr, movie);
                ctr++;
            }
        }
    }

    private void addTags(String datasetFolderName) throws FileNotFoundException {
        try (Scanner scanner = new Scanner(new File(datasetFolderName + "/tags.csv"))){
            scanner.nextLine();
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();
                String[] data = line.split(",");
                int movieFakeId = Integer.parseInt(data[1]);
                String tag = data[2];
                Integer movieId = moviesFakeRealID.get(movieFakeId);
                Movie movie = movies.get(movieId);
                movie.addTag(tag);

                if(!tags.containsKey(tag)) {
                    tags.put(tag,1);
                }else{
                    tags.put(tag,tags.get(tag)+1);
                }
            }
        }
    }
}

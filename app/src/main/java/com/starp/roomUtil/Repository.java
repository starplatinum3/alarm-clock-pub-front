package com.starp.roomUtil;
//package com.example.whatrubbish.db;

import android.content.Context;
//import com.example.whatrubbish.repository.ArticleRepository;
//import com.example.whatrubbish.repository.BasketRepository;
//import com.example.whatrubbish.repository.CardRepository;
//import com.example.whatrubbish.repository.CardGameRepository;
//import com.example.whatrubbish.repository.CityRepository;
//import com.example.whatrubbish.repository.ColeFragGameNowRepository;
//import com.example.whatrubbish.repository.ColeFragGameStatRepository;
//import com.example.whatrubbish.repository.FriendshipRepository;
//import com.example.whatrubbish.repository.GameRepository;
//import com.example.whatrubbish.repository.GameHonorRepository;
//import com.example.whatrubbish.repository.GameRecordRepository;
//import com.example.whatrubbish.repository.PlaceRepository;
//import com.example.whatrubbish.repository.PresentRepository;
//import com.example.whatrubbish.repository.PsnExchgRecRepository;
//import com.example.whatrubbish.repository.RubbishRepository;
//import com.example.whatrubbish.repository.RubbishTypeRepository;
//import com.example.whatrubbish.repository.RubTyCorespRepository;
//import com.example.whatrubbish.repository.ShootGameRepository;
//import com.example.whatrubbish.repository.SignInHonorRepository;
//import com.example.whatrubbish.repository.SignInStdRepository;
//import com.example.whatrubbish.repository.UserRepository;
//import com.example.whatrubbish.repository.WikiHistoryRepository;

import lombok.Data;

@Data
public class Repository {

    Context context;
    //com.example.whatrubbish.db.AppDatabase database;
  RoomUtilAppDatabase database;

    //ArticleRepository articleRepository;
    //
    //BasketRepository basketRepository;
    //
    //CardRepository cardRepository;
    //
    //CardGameRepository cardGameRepository;
    //
    //CityRepository cityRepository;
    //
    //ColeFragGameNowRepository coleFragGameNowRepository;
    //
    //ColeFragGameStatRepository coleFragGameStatRepository;
    //
    //FriendshipRepository friendshipRepository;
    //
    //GameRepository gameRepository;
    //
    //GameHonorRepository gameHonorRepository;
    //
    //GameRecordRepository gameRecordRepository;
    //
    //PlaceRepository placeRepository;
    //
    //PresentRepository presentRepository;
    //
    //PsnExchgRecRepository psnExchgRecRepository;
    //
    //RubbishRepository rubbishRepository;
    //
    //RubbishTypeRepository rubbishTypeRepository;
    //
    //RubTyCorespRepository rubTyCorespRepository;
    //
    //ShootGameRepository shootGameRepository;
    //
    //SignInHonorRepository signInHonorRepository;
    //
    //SignInStdRepository signInStdRepository;
    //
    //UserRepository userRepository;
    //
    //WikiHistoryRepository wikiHistoryRepository;


    public Repository(Context context) {
        this.context = context;
        initDatabase(context);
    }

    void initDatabase(Context context) {
        if (database == null) {
            //database = com.example.whatrubbish.db.AppDatabase.getDatabase(context);
            database = RoomUtilAppDatabase.getDatabase(context);
        }


    }
}


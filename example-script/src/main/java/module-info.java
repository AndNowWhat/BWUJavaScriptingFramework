module com.botwithus.bot.scripts.example {
    requires com.botwithus.bot.api;

    provides com.botwithus.bot.api.BotScript
        with com.botwithus.bot.scripts.example.ExampleScript,
             com.botwithus.bot.scripts.example.WoodcuttingFletcherScript;
}

import com.google.cloud.speech.v1.SpeechSettings;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.IOException;
import java.io.InputStream;
import android.content.Context;

public class SpeechConfig {

    // SpeechSettings을 생성하는 메서드
    public static SpeechSettings getSpeechSettings(Context context) {
        try {
            // assets 폴더에서 인증 파일을 가져오기
            InputStream credentialsStream = context.getAssets().open("service-account.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);

            // SpeechSettings 객체를 반환
            return SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            // 예외 처리: 인증 파일을 로드할 수 없을 경우
            return null;
        }
    }
}

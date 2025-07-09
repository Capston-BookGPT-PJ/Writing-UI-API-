package com.example.writing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.writing.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

//ChatGPT API
import okhttp3.*;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.writing.BuildConfig;

public class UploadAudio extends AppCompatActivity {
    private String apiKey;  // apiKey는 이제 onCreate()에서 초기화
    private Request request;  // request는 callAPI() 메서드 내에서 생성
    private static final int REQUEST_PERMISSION_CODE = 1001;
    private ImageButton btnRecord, btnAddFile, btnLike, btnHashtag, btnUpload;
    private EditText etInput;
    private Button btnSummarize;
    private LinearLayout middleLayout, bottomLayout;
    private boolean isKeyboardVisible = false;
    private ImageView imageView;
    private StorageReference storageReference;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private SpeechRecognizer speechRecognizer;
    final int PERMISSION = 1;
    private Intent intent;
    private Intent speechRecognizerIntent;

    private ImageView micImageView, summarizingImageView;

    // ChatGPT API client setup
    private OkHttpClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        apiKey = BuildConfig.OPENAI_API_KEY;

        ///안드로이드 6.0버전 이상인지 체크해서 퍼미션
        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        storageReference = FirebaseStorage.getInstance().getReference("audio");

        btnUpload = findViewById(R.id.btnUpload);
        btnAddFile = findViewById(R.id.btnAddFile);
        btnLike = findViewById(R.id.btnLike);
        btnHashtag = findViewById(R.id.btnHashtag);
        imageView = findViewById(R.id.imageView);
        etInput = findViewById(R.id.etInput);
        middleLayout = findViewById(R.id.middleLayout);
        bottomLayout = findViewById(R.id.bottomLayout);
        btnRecord = findViewById(R.id.btnRecord);
        btnSummarize = findViewById(R.id.btnSummarize);

        micImageView = findViewById(R.id.micON);
        summarizingImageView = findViewById(R.id.summarizing);

        // Initialize OkHttpClient for ChatGPT API
        client = new OkHttpClient();

        // 음성 인식 권한 확인
        checkPermissions();

        // editText 기능
        etInput.setOnClickListener(v -> {
            if (isKeyboardVisible) {
                // 키보드가 보이면 숨김
                hideKeyboard();
            } else {
                // 키보드가 숨겨져 있으면 보임
                etInput.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etInput, InputMethodManager.SHOW_IMPLICIT);
            }
            isKeyboardVisible = !isKeyboardVisible; // 키보드 상태 반전
        });

        // 첨부 파일 추가
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    }
                });

        btnAddFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            imagePickerLauncher.launch(intent);
        });

        checkPermissions();

        btnLike.setOnClickListener(v -> {
            // 좋아요 기능 구현
        });

        btnHashtag.setOnClickListener(v -> {
            // 해시태그 기능 구현
        });

        // EditText에 입력 감지하는 TextWatcher 추가
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력된 텍스트 길이가 5자 이상이면 버튼 활성화
                if (s.length() >= 5) {
                    btnSummarize.setVisibility(View.VISIBLE);  // 입력 5자 이상 → 버튼 보이기
                } else {
                    btnSummarize.setVisibility(View.GONE);  // 5자 미만 → 버튼 숨기기
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 요약하기 버튼 클릭 리스너
        btnSummarize.setOnClickListener(v -> {
            //요약하기 버튼 숨기기
            btnSummarize.setVisibility(View.GONE);
            // 요약 중 이미지를 보이게
            summarizingImageView.setVisibility(View.VISIBLE);

            // 텍스트를 요약하는 로직
            String inputText = etInput.getText().toString();
            callAPI(inputText);  // ChatGPT API 호출
        });

        /// RecognizerIntent 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR"); // 언어 설정
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString()); // 기기의 기본 언어로 설정


        // btnRecord 클릭 리스너에서 micON 이미지 뷰를 표시
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSummarize.setVisibility(View.GONE);
                initSpeechRecognizer();
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(UploadAudio.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
                speechRecognizer.setRecognitionListener(listener); // 리스너 설정
                // micON 뷰를 찾고 visibility를 VISIBLE로 변경
                if (micImageView != null) {
                    micImageView.setVisibility(View.VISIBLE);  // micON 이미지 뷰를 보이도록 설정
                }
                etInput.setHint("");  // hint를 빈 문자열로 설정
                speechRecognizer.startListening(intent); // 듣기 시작
            }
        });

        checkPermissions();

    }

    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            showSpeechRecognitionUI();
            // 말하기 시작할 준비가되면 호출
        }

        @Override
        public void onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // 입력받는 소리의 크기를 알려줌
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        @Override
        public void onEndOfSpeech() {
            // 말하기를 중지하면 호출
        }

        @Override
        public void onError(int error) {
            Log.e("SpeechRecognizer", "오류 코드: " + error); // 오류 코드 확인
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 녹음 오류";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 오류";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "권한 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 오류";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트워크 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "음성을 인식하지 못했습니다. 다시 시도해 주세요.";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "음성 인식기가 사용 중입니다. 잠시 후 다시 시도해 주세요.";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버 오류 발생";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "입력 시간이 초과되었습니다. 다시 시도해 주세요.";
                    break;
                default:
                    message = "알 수 없는 오류 발생 (코드: " + error + ")";
                    break;
            }

            Log.e("SpeechRecognizer", "에러 메시지: " + message); // 에러 메시지 출력 확인
            Toast.makeText(getApplicationContext(), "에러 발생 : " + message, Toast.LENGTH_SHORT).show();
            hideSpeechRecognitionUI();
            etInput.setHint("독서 후 느낌을 공유해 보세요!");  // 기본 hint로 설정
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                etInput.setText(matches.get(0)); // 인식된 첫 번째 텍스트를 etInput에 설정
                etInput.setSelection(etInput.getText().length()); // 🔥 커서를 맨 뒤로 이동
                // 음성 인식이 완료되면 요약하기 버튼을 보이게 설정
                btnSummarize.setVisibility(View.VISIBLE);
            }
            hideSpeechRecognitionUI();  // 음성 인식 후 UI 숨김
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    };

    //음성인식 초기화 한 번만
    private void initSpeechRecognizer() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(listener);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etInput.getWindowToken(), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 허용 완료", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "권한을 허용해야 음성 인식이 가능합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSpeechRecognitionUI() {
        // 음성 인식 중 이미지 보이기
        if (micImageView != null) {
            micImageView.setVisibility(View.VISIBLE);  // micON 이미지를 보이도록 설정
        }
    }

    // 음성 인식 종료 후 UI 숨김
    private void hideSpeechRecognitionUI() {
        // micON을 찾아서 숨김
        if (micImageView != null) {
            micImageView.setVisibility(View.GONE);  // micON 이미지 뷰를 숨김
        }
    }

    // ChatGPT API 호출
    private void callAPI(String question) {
        // 요약 중 이미지를 보이게
        summarizingImageView.setVisibility(View.VISIBLE);

        JSONObject object = new JSONObject();
        try {
            object.put("model", "gpt-3.5-turbo");
            JSONArray messagesArray = new JSONArray();

            // 시스템 역할 추가
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant that summarizes text.");
            messagesArray.put(systemMessage);

            // 사용자 입력 추가
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", "user");
            messageObj.put("content", "다음 내용을 사용자의 감상을 중심으로 요약해줘:\\n" + question);  // 명확한 요청 추가
            messagesArray.put(messageObj);
            object.put("messages", messagesArray);
            object.put("temperature", 0.7); // 다양성을 조절하는 옵션
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(object.toString(), MediaType.get("application/json; charset=utf-8"));
        request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)  // apiKey를 사용
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // 요청 실패 처리
                runOnUiThread(() -> {
                    summarizingImageView.setVisibility(View.GONE);  // 요약 중 이미지 숨기기
                    Toast.makeText(UploadAudio.this, "요약 실패", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d("API_RESPONSE", responseBody);  // 응답 로깅


                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        String summarizedText = choices.getJSONObject(0).getJSONObject("message").getString("content");

                        runOnUiThread(() -> {
                            etInput.setText(summarizedText);
                            summarizingImageView.setVisibility(View.GONE);
                            btnSummarize.setVisibility(View.VISIBLE);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> {
                        summarizingImageView.setVisibility(View.GONE);
                        Toast.makeText(UploadAudio.this, "API 호출 오류: " + responseBody, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}

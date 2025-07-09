package com.example.myapplication;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1.SpeechSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.google.cloud.speech.v1.RecognizeRequest;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;

import com.google.protobuf.ByteString;

public class UploadAudio extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1000;
    private MediaRecorder mediaRecorder;
    private String audioFilePath;
    private StorageReference storageReference;

    private Button recordButton, stopButton, uploadButton, fetchTextButton;
    private TextView transcribedTextView;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_audio);

        FirebaseApp.initializeApp(this);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storageReference = storage.getReference("audio_uploads");

        recordButton = findViewById(R.id.recordButton);
        stopButton = findViewById(R.id.stopButton);
        uploadButton = findViewById(R.id.uploadButton);
        fetchTextButton = findViewById(R.id.fetchTextButton);
        transcribedTextView = findViewById(R.id.transcribedTextView);

        stopButton.setEnabled(false);
        uploadButton.setEnabled(false);

        recordButton.setOnClickListener(view -> startRecording());
        stopButton.setOnClickListener(view -> stopRecording());
        uploadButton.setOnClickListener(view -> uploadAudioToFirebase());
        fetchTextButton.setOnClickListener(view -> fetchTranscribedText());
    }

    private SpeechSettings getSpeechSettings(Context context) {
        try {
            InputStream credentialsStream = context.getAssets().open("service-account.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
            return SpeechSettings.newBuilder()
                    .setCredentialsProvider(() -> credentials)
                    .build();
        } catch (IOException e) {
            Log.e("UploadAudio", "SpeechSettings 초기화 오류: " + e.getMessage());
            return null;
        }
    }

    private void startRecording() {
        if (checkPermissions()) {
            File audioFile = getAudioFile();
            if (audioFile == null) {
                Toast.makeText(this, "파일 생성 실패", Toast.LENGTH_SHORT).show();
                return;
            }
            audioFilePath = audioFile.getAbsolutePath();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(audioFilePath);

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
                recordButton.setEnabled(false);
                stopButton.setEnabled(true);
                uploadButton.setEnabled(false);
                Toast.makeText(this, "녹음 시작", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Log.e("UploadAudio", "녹음 오류: " + e.getMessage());
            }
        } else {
            requestPermissions();
        }
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            stopButton.setEnabled(false);
            uploadButton.setEnabled(true);
            Toast.makeText(this, "녹음 저장 완료: " + audioFilePath, Toast.LENGTH_SHORT).show();
            Log.d("UploadAudio", "파일 저장 위치: " + audioFilePath);
        }
    }

    private void uploadAudioToFirebase() {
        if (audioFilePath == null) {
            Toast.makeText(this, "녹음 파일이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = Uri.fromFile(new File(audioFilePath));
        StorageReference fileRef = storageReference.child(fileUri.getLastPathSegment());

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "업로드 성공!", Toast.LENGTH_SHORT).show();
                    Log.d("UploadAudio", "파일 업로드 성공: " + fileUri.getLastPathSegment());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("UploadAudio", "업로드 오류: " + e.getMessage());
                });
    }

    private void fetchTranscribedText() {
        if (audioFilePath == null) {
            Toast.makeText(this, "파일을 업로드한 후 시도하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = new File(audioFilePath).getName();
        firestore.collection("transcriptions").document(fileName)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String transcribedText = documentSnapshot.getString("text");
                        transcribedTextView.setText(transcribedText);
                    } else {
                        transcribedTextView.setText("변환된 텍스트를 찾을 수 없습니다.");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "데이터 가져오기 실패", Toast.LENGTH_SHORT).show();
                    Log.e("UploadAudio", "데이터 가져오기 오류: " + e.getMessage());
                });
    }

    private File getAudioFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "AUDIO_" + timeStamp + ".mp4";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        return new File(storageDir, fileName);
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한 허용 완료", Toast.LENGTH_SHORT).show();
                startRecording();
            } else {
                Toast.makeText(this, "권한을 허용해야 녹음이 가능합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Google Speech-to-Text 변환
    private void transcribeAudio(File audioFile) {
        SpeechClient speechClient = null;
        try {
            speechClient = SpeechClient.create();

            // 오디오 파일을 ByteString으로 변환
            FileInputStream audioStream = new FileInputStream(audioFile);
            ByteString audioBytes = ByteString.readFrom(audioStream);

            // RecognitionConfig 설정 (언어, 인코딩 방식 등)
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.MP3)  // MP3로 설정
                    .setSampleRateHertz(16000)  // 샘플 레이트가 16kHz인 경우
                    .setLanguageCode("en-US")  // 사용할 언어 설정
                    .build();

            // RecognitionAudio 설정 (오디오 파일의 콘텐츠)
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(audioBytes)  // 오디오 파일을 ByteString으로 설정
                    .build();

            // 음성 인식 요청 생성
            RecognizeRequest request = RecognizeRequest.newBuilder()
                    .setConfig(config)
                    .setAudio(audio)
                    .build();

            // 음성 인식 실행
            RecognizeResponse response = speechClient.recognize(request);

            // 변환된 텍스트 출력
            StringBuilder transcribedText = new StringBuilder();
            for (SpeechRecognitionResult result : response.getResultsList()) {
                for (SpeechRecognitionAlternative alternative : result.getAlternativesList()) {
                    transcribedText.append(alternative.getTranscript()).append("\n");
                }
            }

            // 변환된 텍스트를 Firestore에 저장
            saveTranscribedTextToFirestore(audioFile.getName(), transcribedText.toString());

            // 변환된 텍스트를 UI에 표시
            transcribedTextView.setText(transcribedText.toString());

        } catch (IOException e) {
            Log.e("UploadAudio", "Google Speech-to-Text API 오류: " + e.getMessage());
            Toast.makeText(this, "변환 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (speechClient != null) {
                speechClient.close(); // 리소스 해제
            }
        }
    }

    private void saveTranscribedTextToFirestore(String fileName, String transcribedText) {
        firestore.collection("transcriptions")
                .document(fileName)
                .set(new Transcription(fileName, transcribedText))
                .addOnSuccessListener(aVoid -> Log.d("UploadAudio", "변환된 텍스트 Firestore 저장 완료"))
                .addOnFailureListener(e -> Log.e("UploadAudio", "Firestore 저장 실패: " + e.getMessage()));
    }

    public static class Transcription {
        private String fileName;
        private String text;

        public Transcription(String fileName, String text) {
            this.fileName = fileName;
            this.text = text;
        }

        public String getFileName() {
            return fileName;
        }

        public String getText() {
            return text;
        }
    }
}

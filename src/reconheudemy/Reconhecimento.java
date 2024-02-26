package reconheudemy;

import java.awt.event.KeyEvent;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_PLAIN;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_face;
import org.bytedeco.javacpp.opencv_face.FaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createEigenFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createFisherFaceRecognizer;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import org.bytedeco.javacpp.opencv_highgui;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.putText;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import sun.java2d.Disposer;

public class Reconhecimento {

    
    static boolean activeInforFinal = false;
    static String name = null;
    static int predicao = 0;
    static CanvasFrame cFrame;
    static Frame frameCapturado = null;

    public static void main(String[] args) throws FrameGrabber.Exception, InterruptedException {

        KeyEvent tecla = null;
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        //OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        //FrameGrabber camera = new FFmpegFrameGrabber("rtsp://192.168.1.10:554/user=admin_password=_channel=2_stream=0.sdp?real_stream_out.avi");
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
        final int captureWidth = 850;
        final int captureHeight = 850;
        camera.setImageWidth(captureWidth);
        camera.setImageHeight(captureHeight);

        String[] pessoas = {"", "Leandro", "Alex"};
        camera.start();

        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade-frontalface-alt.xml");

//        FaceRecognizer reconhecedorEigen = createEigenFaceRecognizer();
//        reconhecedorEigen.load("src\\recursos\\classificadorEigenFaces.yml");
//        FaceRecognizer reconhecedorFish = createFisherFaceRecognizer();
//        reconhecedorFish.load("src\\recursos\\classificadorFisherFaces.yml");
//        reconhecedorFish.setThreshold(100); //quanto menor esse paramentro maior a precisão
        FaceRecognizer reconhecedorLB = createLBPHFaceRecognizer();
        reconhecedorLB.load("src\\recursos\\classificadorLBPH.yml");
        reconhecedorLB.setThreshold(20);

        CanvasFrame cFrame = new CanvasFrame("Reconhecimento", CanvasFrame.getDefaultGamma() / camera.getGamma());
        //cFrame = new CanvasFrame("Reconhecimento");

        Mat imagemColorida = new Mat();
        RectVector facesDetectadas = null;
        while ((frameCapturado = camera.grab()) != null) {

            imagemColorida = converteMat.convert(frameCapturado);
            Mat imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
            facesDetectadas = new RectVector();

            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.1, 1, 0, new Size(200, 200), new Size(500, 500));

            for (int i = 0; i < facesDetectadas.size(); i++) {
                Rect dadosFace = facesDetectadas.get(0);

                rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));

                Mat faceCapturada = new Mat(imagemCinza, dadosFace);

                resize(faceCapturada, faceCapturada, new Size(500, 500));//160 160 conforme resize de captura

                IntPointer rotulo = new IntPointer(1);
                DoublePointer confianca = new DoublePointer(1);
                reconhecedorLB.predict(faceCapturada, rotulo, confianca);
                int predicao = rotulo.get(0);
                String nome;
                if (predicao == -1) {
                    nome = "identidade Desconhecida";

                } else {
                      nome = pessoas[predicao];
//                    nome = pessoas[predicao] + " - " + confianca.get(0);
                    //System.out.println(nome);

                }

                int x = Math.max(dadosFace.tl().x() - 10, 0);
                int y = Math.max(dadosFace.tl().y() - 10, 0);

                putText(imagemColorida, nome, new Point(x, y), FONT_HERSHEY_PLAIN, 1.4, new Scalar(0, 255, 0, 0));

                

            }

            if (cFrame.isVisible()) {
                Thread t2;
                t2 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cFrame.showImage(frameCapturado);
                    }
                });
                t2.run();
            }
            if(tecla==null){
                tecla = cFrame.waitKey(1);
            }
            
            if (tecla != null) {

                    if (tecla.getKeyChar() == 'c') {
                        camera.close();
                        System.exit(0);
                    }
            tecla = null;    
            }
            
        }

        cFrame.dispose();
        camera.stop();

    }

}

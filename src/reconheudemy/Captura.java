package reconheudemy;

import java.awt.event.KeyEvent;
import java.util.Scanner;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_core.RectVector;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.COLOR_BGRA2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;
import org.bytedeco.javacpp.opencv_objdetect.CascadeClassifier;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameGrabber;

public class Captura {

    public static void main(String args[]) throws FrameGrabber.Exception, InterruptedException {

        KeyEvent tecla = null;

        final int captureWidth = 700;
        final int captureHeight = 700;
        OpenCVFrameConverter.ToMat converteMat = new OpenCVFrameConverter.ToMat();
        OpenCVFrameGrabber camera = new OpenCVFrameGrabber(0);
      //  FrameGrabber camera = new FFmpegFrameGrabber("rtsp://192.168.1.10:554/user=admin_password=_channel=1_stream=0.sdp?real_stream");
       // FrameGrabber camera = new FFmpegFrameGrabber("rtsp://192.168.1.10:554/user=admin&password=channel=0_stream=0.sdp");
        camera.setImageWidth(captureWidth);
        camera.setImageHeight(captureHeight);
        camera.start();

        CascadeClassifier detectorFace = new CascadeClassifier("src\\recursos\\haarcascade-frontalface-alt.xml");
      //  CascadeClassifier detectorFace = new CascadeClassifier("C:\\Users\\Leandro\\Desktop\\reconheUdemy\\src\\recursos\\haarcascade-frontalface-alt.xml");

        CanvasFrame cFrame = new CanvasFrame("Preview", CanvasFrame.getDefaultGamma() / camera.getGamma());
     //   CanvasFrame cFrame = new CanvasFrame("Preview");
        Frame frameCapturado = null;
        cFrame.setFocusable(true);
        Mat imagemColorida = new Mat();
        Mat imagemCinza;
        int numeroAmostra = 25;
        int amostra = 1;

        System.out.println("digite o seu id : ");
        Scanner cadastro = new Scanner(System.in);
        int idPessoa = cadastro.nextInt();

        while ((frameCapturado = camera.grab()) != null) {

            imagemColorida = converteMat.convert(frameCapturado);
            imagemCinza = new Mat();
            cvtColor(imagemColorida, imagemCinza, COLOR_BGRA2GRAY);
            RectVector facesDetectadas = new RectVector();
            detectorFace.detectMultiScale(imagemCinza, facesDetectadas, 1.4, 1, 0, new Size(200, 200), new Size(500, 500));

//            if (tecla == null) {
//                tecla = cFrame.waitKey(1);
//            } else {
//                System.out.println("." + tecla.getKeyChar() + "-"
//                        + tecla.getSource());
//            }

            for (int i = 0; i < facesDetectadas.size(); i++) {
               
                Rect dadosFace = facesDetectadas.get(0);
                rectangle(imagemColorida, dadosFace, new Scalar(0, 0, 255, 0));

                Mat faceCapturada = new Mat(imagemCinza, dadosFace);

                resize(faceCapturada, faceCapturada, new Size(500, 500));//tem que igual ao do reconhecimento

//                if (tecla == null) {
//                    tecla = cFrame.waitKey(1);
//                }

               // if (tecla != null) {

                   // if (tecla.getKeyChar() == 'c') {
                        System.out.println("capturada");
                        if (amostra <= numeroAmostra) {
                              imwrite("src\\fotos\\pessoa." + idPessoa + "." + amostra + ".jpg", faceCapturada);
                          //  imwrite("C:\\Users\\Leandro\\Desktop\\reconheUdemy\\src\\fotos\\pessoa." + idPessoa + "." + amostra + ".jpg", faceCapturada);
                            
                            System.out.println("Foto " + amostra + "capturada\n");
                           
                            amostra++;
                        }

                   // }

                  //  tecla = null;
               // }

            }

//            if (tecla == null) {
//                tecla = cFrame.waitKey(2);
//            }

            if (cFrame.isVisible()) {

                cFrame.showImage(frameCapturado);
            }

            if (amostra > numeroAmostra) {

                break;
            }

        }
        cFrame.dispose();
        camera.stop();

    }

}

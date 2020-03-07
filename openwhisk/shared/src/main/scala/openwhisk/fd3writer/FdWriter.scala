package openwhisk.fd3writer

private [openwhisk] object Fd3Writer {
  def write(obj: ujson.Obj): Unit = {
    ujson.writeTo(obj, Fd3WriterImpl.writer)
    Fd3WriterImpl.writer.write('\n')
    Fd3WriterImpl.writer.flush()
  }
}

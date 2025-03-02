import { Card, CardContent } from "@/components/ui/card";
import { Loader2 } from "lucide-react";

const CardLoader = () => {
    return (
        <Card className="w-full">
            <CardContent className="p-8 flex justify-center">
                <Loader2 className="h-8 w-8 animate-spin" />
            </CardContent>
        </Card>
    );
}

export default CardLoader;
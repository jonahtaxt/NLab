import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

interface CardErrorRetryProps {
    error: string;
    retry: () => void;
};

const CardErrorRetry = ({
    error,
    retry
}: CardErrorRetryProps) => {
    return (
        <Card className="w-full">
            <CardContent className="p-8">
                <div className="text-red-500">{error}</div>
                <Button onClick={retry} className="mt-4">Retry</Button>
            </CardContent>
        </Card>
    );
};

export default CardErrorRetry;